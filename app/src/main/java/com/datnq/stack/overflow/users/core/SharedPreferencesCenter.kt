package com.datnq.stack.overflow.users.core

import android.content.Context
import android.util.Base64
import com.datnq.stack.overflow.users.datnq.library.secure.preferences.SecurePreferences
import java.nio.charset.StandardCharsets
import java.util.*

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
        private const val PREF_FILE_USERNAME = "PREFERENCES_GICO_FILE_USERNAME"
        private const val PREF_PREFERENCES_FILE = "PREFERENCES_GICO_PREFERENCES_FILE"
        private const val SHARED_PREFERENCES_FILE_NAME = "SHARED_PREFERENCES_GICO_FILE_NAME"
    }

}