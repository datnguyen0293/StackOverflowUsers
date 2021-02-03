package com.datnq.stack.overflow.users.core

import android.content.Context
import android.util.Base64
import com.datnq.stack.overflow.users.datnq.library.secure.preferences.SecurePreferences
import com.datnq.stack.overflow.users.hotel.GiHotelApplication
import com.datnq.stack.overflow.users.hotel.R
import com.datnq.stack.overflow.users.hotel.model.LoginInformation
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

class SharedPreferencesCenter(context: Context) {

    private var mSecurePreferences: SecurePreferences? = null

    init {
        var basicAuth = java.lang.String.format(
                "%s:%s", PREF_FILE_USERNAME, PREF_PREFERENCES_FILE)
        basicAuth = java.lang.String.format(Locale.getDefault(), "Basic %s",
                Base64.encodeToString(basicAuth.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP))
        mSecurePreferences = SecurePreferences(context, basicAuth, SHARED_PREFERENCES_FILE_NAME)
    }

    companion object {
        private const val PREF_LANGUAGE = ".pref.gico.language"
        private const val PREF_IP = ".pref.gico.ip"
        private const val PREF_TOKEN = ".pref.gico.token"
        private const val PREF_DEVICE_TOKEN = ".pref.gico.device.token"
        private const val PREF_ACCOUNT = ".pref.gico.account"
        private const val PREF_CURRENT_ACCOUNT = ".pref.gico.current.account"
        private const val PREF_NEED_UPDATE_DATABASE = ".pref.gico.need.update.database"
        private const val PREF_SHOW_ACCOUNT_INSTRUCTION = ".pref.gico.show.instruction"
        private const val PREF_CURRENT_LOCATION = ".pre.current.location"
        private const val PREF_ALERT_ADDRESS = ".pre.alert.address"
        private const val PREF_ALERT_TYPE = ".pre.alert.type"
        private const val PREF_FILE_USERNAME = "PREFERENCES_GICO_FILE_USERNAME"
        private const val PREF_PREFERENCES_FILE = "PREFERENCES_GICO_PREFERENCES_FILE"
        private const val SHARED_PREFERENCES_FILE_NAME = "SHARED_PREFERENCES_GICO_FILE_NAME"
    }

    fun getLanguage(): String {
        mSecurePreferences?.getString(PREF_LANGUAGE, Locale.getDefault().language)?.let { return it }
        return Locale.getDefault().language
    }

    fun setLanguage(language: String) {
        if (language.isNotEmpty()) {
            mSecurePreferences?.edit()?.putString(PREF_LANGUAGE, language)?.apply()
        }
    }

    fun getWanIp(defaultIp: String): String {
        mSecurePreferences?.getString(PREF_IP, defaultIp)?.let { return it }
        return defaultIp
    }

    fun setWanIp(ip: String) {
        if (ip.isNotEmpty()) {
            mSecurePreferences?.edit()?.putString(PREF_IP, ip)?.apply()
        }
    }

    fun getToken(): String {
        mSecurePreferences?.getString(PREF_TOKEN, Constants.EMPTY_STRING)?.let { return it }
        return Constants.EMPTY_STRING
    }

    fun setToken(token: String) {
        if (token.isNotEmpty()) {
            mSecurePreferences?.edit()?.putString(PREF_TOKEN, token)?.apply()
        }
    }

    fun getDeviceToken(): String {
        mSecurePreferences?.getString(PREF_DEVICE_TOKEN, Constants.EMPTY_STRING)?.let { return it }
        return Constants.EMPTY_STRING
    }

    fun setDeviceToken(token: String) {
        if (token.isNotEmpty()) {
            mSecurePreferences?.edit()?.putString(PREF_DEVICE_TOKEN, token)?.apply()
        }
    }

    fun getAccounts(): ArrayList<LoginInformation> {
        mSecurePreferences?.getString(PREF_ACCOUNT, "[]")?.let {
            return if ("[]".equals(it, false)) {
                ArrayList()
            } else {
                GiHotelApplication.gSon().fromJson(it, object : TypeToken<ArrayList<LoginInformation>>() {}.type)
            }
        }
        return ArrayList()
    }

    fun setAccounts(accounts: ArrayList<LoginInformation>) {
        if (accounts.isNotEmpty()) {
            mSecurePreferences?.edit()?.putString(PREF_ACCOUNT, GiHotelApplication.gSon().toJson(accounts, object : TypeToken<ArrayList<LoginInformation>>() {}.type))?.apply()
        } else {
            mSecurePreferences?.edit()?.putString(PREF_ACCOUNT, "[]")?.apply()
        }
    }

    fun getCurrentAccount(): LoginInformation {
        val json = mSecurePreferences?.getString(PREF_CURRENT_ACCOUNT, GiHotelApplication.getInstance().getString(R.string.app_name))
        if (!json.equals(GiHotelApplication.getInstance().getString(R.string.app_name), false)) {
            return GiHotelApplication.gSon().fromJson(json, LoginInformation::class.java)
        }
        return LoginInformation()
    }

    fun setCurrentAccount(loginInformation: LoginInformation) {
        mSecurePreferences?.edit()?.putString(PREF_CURRENT_ACCOUNT, GiHotelApplication.gSon().toJson(loginInformation, LoginInformation::class.java))?.apply()
    }

    fun getNeedUpdateDatabase(): Int {
        mSecurePreferences?.getInt(PREF_NEED_UPDATE_DATABASE, 0)?.let { return it }
        return 0
    }

    fun setNeedUpdateDatabase(flag: Int) {
        mSecurePreferences?.edit()?.putInt(PREF_NEED_UPDATE_DATABASE, flag)?.apply()
    }

    fun isShowInstruction(): Boolean {
        mSecurePreferences?.getBoolean(PREF_SHOW_ACCOUNT_INSTRUCTION, true)?.let { return it }
        return true
    }

    fun setShowInstruction(isShowInstruction: Boolean) {
        mSecurePreferences?.edit()?.putBoolean(PREF_SHOW_ACCOUNT_INSTRUCTION, isShowInstruction)?.apply()
    }

    fun getCurrentLocation(): String {
        mSecurePreferences?.getString(PREF_CURRENT_LOCATION, Constants.EMPTY_STRING)?.let { return it }
        return Constants.EMPTY_STRING
    }

    fun setCurrentLocation(location: String) {
        if (location.isNotEmpty()) {
            mSecurePreferences?.edit()?.putString(PREF_CURRENT_LOCATION, location)?.apply()
        }
    }

    fun getAlertAddress(): String {
        mSecurePreferences?.getString(PREF_ALERT_ADDRESS, Constants.EMPTY_STRING)?.let { return it }
        return Constants.EMPTY_STRING
    }

    fun setAlertAddress(alertAddress: String) {
        if (alertAddress.isNotEmpty()) {
            mSecurePreferences?.edit()?.putString(PREF_ALERT_ADDRESS, alertAddress)?.apply()
        }
    }

    fun getAlertType(): String {
        mSecurePreferences?.getString(PREF_ALERT_TYPE, Constants.EMPTY_STRING)?.let { return it }
        return Constants.EMPTY_STRING
    }

    fun setAlertType(alertType: String) {
        mSecurePreferences?.edit()?.putString(PREF_ALERT_TYPE, alertType)?.apply()
    }
}