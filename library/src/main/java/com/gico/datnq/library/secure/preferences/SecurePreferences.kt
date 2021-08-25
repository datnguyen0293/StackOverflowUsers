/*
 * Copyright (C) 2015, Scott Alexander-Bown, Daniel Abraham
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("DEPRECATION")

package com.gico.datnq.library.secure.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import com.gico.datnq.library.BuildConfig
import com.gico.datnq.library.utilities.LoggerUtil
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Wrapper class for Android's {@link SharedPreferences} interface, which adds a
 * layer of encryption to the persistent storage and retrieval of sensitive
 * key-value pairs of primitive data types.
 * <p>
 * This class provides important - but nevertheless imperfect - protection
 * against simple attacks by casual snoopers. It is crucial to remember that
 * even encrypted data may still be susceptible to attacks, especially on rooted devices
 * <p>
 * Recommended to use with user password, in which case the key will be derived from the password and not
 * stored in the file.
 * <p>
 */
class SecurePreferences : SharedPreferences {

    //the backing pref file
    private var sharedPreferences: SharedPreferences

    //secret keys used for enc and dec
    private lateinit var keys: SecretKeys

    //the salt used for enc and dec
    private var sSalt: String? = null

    companion object {
        val sLoggingEnabled: Boolean = BuildConfig.DEBUG

        /**
         * Gets the hardware serial number of this device.
         *
         * @return serial number or Settings.Secure.ANDROID_ID if not available.
         */
        @SuppressLint("HardwareIds")
        @JvmStatic
        private fun getDeviceSerialNumber(context: Context): String {
            // We're using the Reflection API because Build.SERIAL is only available
            // since API Level 9 (Gingerbread, Android 2.3).
            return try {
                val deviceSerial = Build::class.java.getField("SERIAL").get(null) as String
                if (TextUtils.isEmpty(deviceSerial)) {
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                } else {
                    deviceSerial
                }
            } catch (e: Exception) {
                // Fall back  to Android_ID
                LoggerUtil.e(SecurePreferences::class.java.simpleName, "getDeviceSerialNumber", e)
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            }
        }

        /**
         * The Pref keys must be same each time so we're using a hash to obscure the stored value
         *
         * @return SHA-256 Hash of the preference key
         */
        @JvmStatic
        private fun hashPrefKey(prefKey: String): String? {
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val bytes = prefKey.toByteArray(StandardCharsets.UTF_8)
                digest.update(bytes, 0, bytes.size)

                return Base64.encodeToString(digest.digest(), SecretKeys.BASE64_FLAGS)
            } catch (e: NoSuchAlgorithmException) {
                if (sLoggingEnabled) {
                    LoggerUtil.e(SecurePreferences::class.java.simpleName, "Problem generating hash", e)
                }
            }
            return null
        }
    }

    /**
     * @param context should be ApplicationContext not Activity
     * @param password user password/code used to generate encryption key.
     * @param sharedPrefFilename name of the shared pref file. If null use the default shared prefs
     */
    constructor(context: Context, password: String, sharedPrefFilename: String) :
            this(context, password, null, sharedPrefFilename)

    /**
     * @param context should be ApplicationContext not Activity
     */
    constructor(context: Context, password: String, salt: String?, sharedPrefFilename: String) :
            this(context, null, password, salt, sharedPrefFilename)

    constructor(context: Context, secretKey: SecretKeys?, password: String, salt: String?, sharedPrefFilename: String) {

        sharedPreferences = getSharedPreferenceFile(context, sharedPrefFilename)

        salt?.let { sSalt = it }

        secretKey?.let { keys = it } ?: run {
            when {
                TextUtils.isEmpty(password) -> {
                    // Initialize or create encryption key
                    try {
                        val key = generateAesKeyName(context)

                        val keyAsString = sharedPreferences.getString(key, null)
                        if (keyAsString == null) {
                            keys = AesCbcWithIntegrity.generateKey()
                            //saving key
                            val committed = sharedPreferences.edit().putString(key, keys.toString()).commit()
                            if (!committed) {
                                LoggerUtil.i(javaClass.simpleName, "", "Key not committed to prefs")
                            }
                        } else {
                            keys = AesCbcWithIntegrity.keys(keyAsString)
                        }

                    } catch (e: GeneralSecurityException) {
                        if (sLoggingEnabled) {
                            LoggerUtil.e(javaClass.simpleName, "Error init:" + e.localizedMessage, e)
                        }
                        throw IllegalStateException(e)
                    }
                }
                else -> {
                    //use the password to generate the key
                    try {
                        val saltBytes = getSalt(context).toByteArray(StandardCharsets.UTF_8)
                        keys = AesCbcWithIntegrity.generateKeyFromPassword(password, saltBytes)
                    } catch (e: GeneralSecurityException) {
                        if (sLoggingEnabled) {
                            LoggerUtil.e(javaClass.simpleName, "Error init using user password:" + e.localizedMessage, e)
                        }
                        throw IllegalStateException(e)
                    }
                }
            }
        }
    }

    /**
     * if a prefFilename is not defined the getDefaultSharedPreferences is used.
     *
     * @param context should be ApplicationContext not Activity
     */
    private fun getSharedPreferenceFile(context: Context, prefFilename: String): SharedPreferences {

        return if (TextUtils.isEmpty(prefFilename)) {
            PreferenceManager.getDefaultSharedPreferences(context)
        } else {
            context.getSharedPreferences(prefFilename, Context.MODE_PRIVATE)
        }
    }

    /**
     * Uses device and application values to generate the pref key for the encryption key
     *
     * @param context should be ApplicationContext not Activity
     * @return String to be used as the AESkey Pref key
     * @throws GeneralSecurityException if something goes wrong in generation
     */
    private fun generateAesKeyName(context: Context): String? {
        val password = context.packageName
        val salt = getSalt(context).toByteArray(StandardCharsets.UTF_8)
        val generatedKeyName = AesCbcWithIntegrity.generateKeyFromPassword(password, salt)

        return hashPrefKey(generatedKeyName.toString())
    }

    /**
     * Gets the salt value
     *
     * @param context used for accessing hardware serial number of this device in case salt is not set
     * @return A string version of the salt base64 encoded.
     */
    private fun getSalt(context: Context): String {
        if (sSalt == null) {
            sSalt = getDeviceSerialNumber(context)
        }
        sSalt?.let { return it }
        return ""
    }

    /**
     * @return decrypted plain text, unless decryption fails, in which case null
     */
    private fun decrypt(ciphertext: String): String? {
        if (TextUtils.isEmpty(ciphertext)) {
            return ciphertext
        }
        try {
            val cipherTextIvMac = CipherTextIvMac(ciphertext)

            return AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys)
        } catch (e: GeneralSecurityException) {
            if (sLoggingEnabled) {
                LoggerUtil.e(javaClass.simpleName, "decrypt", e)
            }
        } catch (e: UnsupportedEncodingException) {
            if (sLoggingEnabled) {
                LoggerUtil.e(javaClass.simpleName, "decrypt", e)
            }
        }
        return null
    }

    override fun getAll(): MutableMap<String, *> {
        //wont be null as per http://androidxref
        // .com/5.1.0_r1/xref/frameworks/base/core/java/android/app/SharedPreferencesImpl.java

        //wont be null as per http://androidxref
        // .com/5.1.0_r1/xref/frameworks/base/core/java/android/app/SharedPreferencesImpl.java
        val encryptedMap = sharedPreferences.all
        val decryptedMap: MutableMap<String, String?> = HashMap(encryptedMap.size)
        for ((key, cipherText) in encryptedMap) {
            try {
                //don't include the key
                if (cipherText != keys.toString()) {
                    //the prefs should all be strings
                    decryptedMap[key] = decrypt(cipherText.toString())
                }
            } catch (e: Exception) {
                if (sLoggingEnabled) {
                    LoggerUtil.e(javaClass.simpleName, "error during getAll", e)
                }

                // Ignore issues that unencrypted values and use instead raw cipher text string
                decryptedMap[key] = cipherText.toString()
            }
        }
        return decryptedMap
    }

    override fun getString(key: String, defaultValue: String?): String? {
        val encryptedValue = sharedPreferences.getString(hashPrefKey(key), null)

        encryptedValue?.let {
            val decryptedValue = decrypt(it)
            decryptedValue?.let { d ->
                return d
            }
        }
        return defaultValue
    }

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? {
        val encryptedSet = sharedPreferences.getStringSet(hashPrefKey(key), null)
                ?: return defValues

        val decryptedSet: MutableSet<String>? = HashSet(encryptedSet.size)
        for (encryptedValue in encryptedSet) {
            encryptedValue?.let { e ->
                decrypt(e)?.let { decryptedSet?.add(it) }
            }
        }
        return decryptedSet
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val encryptedValue = sharedPreferences.getString(hashPrefKey(key), null)
                ?: return defaultValue
        try {
            val decrypt = decrypt(encryptedValue)
            decrypt?.toInt()?.let { return it }
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }
        return defaultValue
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val encryptedValue = sharedPreferences.getString(hashPrefKey(key), null)
                ?: return defaultValue
        try {
            val decrypt = decrypt(encryptedValue)
            decrypt?.toLong()?.let { return it }
        } catch (e: java.lang.NumberFormatException) {
            throw java.lang.ClassCastException(e.message)
        }
        return defaultValue
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val encryptedValue = sharedPreferences.getString(hashPrefKey(key), null)
                ?: return defaultValue
        try {
            val decrypt = decrypt(encryptedValue)
            decrypt?.toFloat()?.let { return it }
        } catch (e: java.lang.NumberFormatException) {
            throw java.lang.ClassCastException(e.message)
        }
        return defaultValue
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val encryptedValue = sharedPreferences.getString(hashPrefKey(key), null)
                ?: return defaultValue
        return try {
            java.lang.Boolean.parseBoolean(decrypt(encryptedValue))
        } catch (e: java.lang.NumberFormatException) {
            throw java.lang.ClassCastException(e.message)
        }
    }

    override fun contains(key: String): Boolean {
        return sharedPreferences.contains(hashPrefKey(key))
    }

    override fun edit(): SecurePreferences.Editor {
        return Editor()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Wrapper for Android's [SharedPreferences.Editor].
     *
     *
     * Used for modifying values in a [SecurePreferences] object. All
     * changes you make in an editor are batched, and not copied back to the
     * original [SecurePreferences] until you call [.commit] or
     * [.apply].
     */
    inner class Editor : SharedPreferences.Editor {
        private val mEditor: SharedPreferences.Editor = sharedPreferences.edit()
        private fun encrypt(cleartext: String): String? {
            if (TextUtils.isEmpty(cleartext)) {
                return cleartext
            }
            try {
                return AesCbcWithIntegrity.encrypt(cleartext, keys).toString()
            } catch (e: GeneralSecurityException) {
                if (sLoggingEnabled) {
                    LoggerUtil.e(SecurePreferences::class.java.simpleName, "encrypt", e)
                }
                return null
            } catch (e: UnsupportedEncodingException) {
                if (sLoggingEnabled) {
                    LoggerUtil.e(SecurePreferences::class.java.simpleName, "encrypt", e)
                }
            }
            return null
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            value?.let {
                mEditor.putString(hashPrefKey(key), encrypt(it))
            }
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            values?.let {
                val encryptedValues: MutableSet<String?> = HashSet(it.size)
                for (value in it) {
                    encryptedValues.add(encrypt(value))
                }
                mEditor.putStringSet(hashPrefKey(key), encryptedValues)
            }
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            mEditor.putString(hashPrefKey(key), encrypt(value.toString()))
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            mEditor.putString(hashPrefKey(key), encrypt(value.toString()))
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            mEditor.putString(hashPrefKey(key), encrypt(value.toString()))
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            mEditor.putString(hashPrefKey(key), encrypt(java.lang.Boolean.toString(value)))
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            mEditor.remove(hashPrefKey(key))
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            mEditor.clear()
            return this
        }

        override fun commit(): Boolean {
            return mEditor.commit()
        }

        override fun apply() {
            mEditor.apply()
        }

    }
}