package com.datnq.stack.overflow.users.core

import android.util.Log
import androidx.annotation.NonNull
import com.datnq.stack.overflow.users.BuildConfig

/**
 * @author Dat Nguyen
 * @since 2019 Sep 13
 */

class LoggerUtil {

    companion object {
        private const val APPLICATION_NAME = "GiHome"
        private const val INFO_CLASS = "INFO: [CLASS: "
        private const val METHOD = ". METHOD: "
        private const val MESSAGE = ". MESSAGE: "
        private const val CLOSE = "]"

        @JvmStatic
        fun d(@NonNull className: String, @NonNull methodName: String, @NonNull message: String) {
            if (BuildConfig.DEBUG) {
                Log.d(APPLICATION_NAME, INFO_CLASS + className + METHOD + methodName
                        + MESSAGE + (if (message.isNotEmpty()) message else "") + CLOSE)
            }
        }

        @JvmStatic
        fun e(@NonNull className: String, @NonNull methodName: String, @NonNull cause: Throwable) {
            if (BuildConfig.DEBUG) {
                Log.e(APPLICATION_NAME, INFO_CLASS + className + METHOD + methodName
                        + MESSAGE + cause.localizedMessage + CLOSE, cause)
            }
        }

        @JvmStatic
        fun i(@NonNull className: String, @NonNull methodName: String, @NonNull message: String) {
            if (BuildConfig.DEBUG) {
                Log.i(APPLICATION_NAME, INFO_CLASS + className + METHOD + methodName
                        + MESSAGE + (if (message.isNotEmpty()) message else "") + CLOSE)
            }
        }

    }
}