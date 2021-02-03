package com.datnq.stack.overflow.users.datnq.library.secure.preferences

import android.os.Build
import com.datnq.stack.overflow.users.datnq.library.utilities.LoggerUtil
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.Provider
import java.security.SecureRandom
import java.security.Security

/**
 * Fixes for the RNG as per
 * http://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
 * <p>
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will Google be held liable for any damages arising
 * from the use of this software.
 * <p>
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, as long as the origin is not misrepresented.
 * <p>
 * Fixes for the output of the default PRNG having low entropy.
 * <p>
 * The fixes need to be applied via {@link #apply()} before any use of Java
 * Cryptography Architecture primitives. A good place to invoke them is in
 * the application's {@code onCreate}.
 */
class PrngFixes {

    companion object {
        private const val VERSION_CODE_JELLY_BEAN = 16
        private const val VERSION_CODE_JELLY_BEAN_MR2 = 18

        // If ALLOW_BROKEN_PRNG is true, however, we will simply log instead.
        private const val ALLOW_BROKEN_PRNG = false
        private const val LINUX_PRING_SECURE_RANDOM_PROVIDER = "LinuxPRNGSecureRandomProvider"
        val BUILD_FINGERPRINT_AND_DEVICE_SERIAL = getBuildFingerprintAndDeviceSerial()

        @JvmStatic
        private fun getBuildFingerprintAndDeviceSerial(): ByteArray {
            val result = StringBuilder()
            val fingerprint = Build.FINGERPRINT
            fingerprint?.let {
                result.append(it)
            }
            val serial = getDeviceSerialNumber()
            serial?.let {
                result.append(it)
            }
            return result.toString().toByteArray(StandardCharsets.UTF_8)
        }

        /**
         * Gets the hardware serial number of this device.
         *
         * @return serial number or {@code null} if not available.
         */
        @JvmStatic
        private fun getDeviceSerialNumber(): String? {
            // We're using the Reflection API because Build.SERIAL is only
            // available since API Level 9 (Gingerbread, Android 2.3).
            try {
                return Build::class.java.getField("SERIAL").get(null) as String
            } catch (e: Exception) {
                LoggerUtil.e("AesCbcWithIntegrity", "getDeviceSerialNumber", e)
                return null
            }
        }

        /**
         * Applies all fixes.
         *
         * @throws SecurityException if a fix is needed but could not be
         *                           applied.
         */
        @JvmStatic
        fun apply() {
            applyOpenSSLFix()
            installLinuxPRNGSecureRandom()
        }

        /**
         * Applies the fix for OpenSSL PRNG having low entropy. Does nothing if
         * the fix is not needed.
         *
         * @throws SecurityException if the fix is needed but could not be
         *                           applied.
         */
        @JvmStatic
        private fun applyOpenSSLFix() {
            if ((Build.VERSION.SDK_INT < VERSION_CODE_JELLY_BEAN)
                    || (Build.VERSION.SDK_INT > VERSION_CODE_JELLY_BEAN_MR2)) {
                // No need to apply the fix
                return
            }

            try {
                // Mix in the device- and invocation-specific seed.
                Class.forName("org.apache.harmony.xnet.provider.jsse.NativeCrypto")
                        .getMethod("RAND_seed", ByteArray::class.java).invoke(null, LinuxPRNGSecureRandom.generateSeed())

                // Mix output of Linux PRNG into OpenSSL's PRNG
                val obj = Class
                        .forName("org.apache.harmony.xnet.provider.jsse.NativeCrypto")
                        .getMethod("RAND_load_file", String::class.java, Long::class.java)
                        .invoke(null, "/dev/urandom", 1024)
                obj?.let {
                    val bytesRead = it as Int
                    if (bytesRead != 1024) {
                        throw IOException("Unexpected number of bytes read from Linux PRNG: "
                                + bytesRead)
                    }
                }
            } catch (e: Exception) {
                if (ALLOW_BROKEN_PRNG) {
                    LoggerUtil.e(PrngFixes::class.java.simpleName, "Failed to seed OpenSSL PRNG", e)
                } else {
                    throw SecurityException("Failed to seed OpenSSL PRNG", e)
                }
            }
        }

        /**
         * Installs a Linux PRNG-backed {@code SecureRandom} implementation as
         * the default. Does nothing if the implementation is already the
         * default or if there is not need to install the implementation.
         *
         * @throws SecurityException if the fix is needed but could not be
         *                           applied.
         */
        @JvmStatic
        private fun installLinuxPRNGSecureRandom() {
            if (Build.VERSION.SDK_INT > VERSION_CODE_JELLY_BEAN_MR2) {
                // No need to apply the fix
                return
            }

            // Install a Linux PRNG-based SecureRandom implementation as the
            // default, if not yet installed.
            val secureRandomProviders: Array<Provider> = Security.getProviders("SecureRandom.SHA1PRNG")

            // Insert and check the provider atomically.
            // The official Android Java libraries use synchronized methods for
            // insertProviderAt, etc., so synchronizing on the class should
            // make things more stable, and prevent race conditions with other
            // versions of this code.
            synchronized(Security::class.java) {
                if (secureRandomProviders.isEmpty() || !secureRandomProviders[0].javaClass.simpleName.equals(LINUX_PRING_SECURE_RANDOM_PROVIDER, false)) {
                    Security.insertProviderAt(LinuxPRNGSecureRandomProvider(), 1)
                }

                // Assert that SecureRandom() and
                // SecureRandom.getInstance("SHA1PRNG") return a SecureRandom backed
                // by the Linux PRNG-based SecureRandom implementation.
                val rng1 = SecureRandom()
                if (!rng1.provider.javaClass.simpleName.equals(LINUX_PRING_SECURE_RANDOM_PROVIDER, false)) {
                    if (ALLOW_BROKEN_PRNG) {
                        LoggerUtil.i(PrngFixes::class.java.simpleName, "SecureRandom()",
                                "SecureRandom() backed by wrong Provider: " + rng1.provider.javaClass)
                        return@installLinuxPRNGSecureRandom
                    } else {
                        throw SecurityException("SecureRandom() backed by wrong Provider: " + rng1.provider.javaClass)
                    }
                }

                try {
                    val rng2 = SecureRandom.getInstance("SHA1PRNG")
                    val provider = rng2.provider.javaClass.simpleName
                    if (!LINUX_PRING_SECURE_RANDOM_PROVIDER.equals(provider, false)) {
                        if (ALLOW_BROKEN_PRNG) {
                            LoggerUtil.i(PrngFixes::class.java.simpleName, "getInstance",
                                    "SecureRandom.getInstance(\"SHA1PRNG\") backed by wrong" + " Provider: "
                                            + rng2.provider.javaClass)
                        } else {
                            throw SecurityException(
                                    "SecureRandom.getInstance(\"SHA1PRNG\") backed by wrong" + " Provider: "
                                            + rng2.provider.javaClass)
                        }
                    }
                } catch (e: NoSuchAlgorithmException) {
                    if (ALLOW_BROKEN_PRNG) {
                        LoggerUtil.e(PrngFixes::class.java.simpleName, "SHA1PRNG not available", e)
                    }
                }
            }
        }
    }
}