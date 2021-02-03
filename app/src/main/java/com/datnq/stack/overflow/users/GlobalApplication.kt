package com.datnq.stack.overflow.users

import androidx.multidex.MultiDex
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class GlobalApplication : DaggerApplication() {

    companion object {
        private lateinit var mInstance: GlobalApplication

        @JvmStatic
        fun getInstance(): GlobalApplication {
            return mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        MultiDex.install(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        TODO("Not yet implemented")
    }
}