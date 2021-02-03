package com.datnq.stack.overflow.users

import androidx.multidex.MultiDex
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class GlobalApplication: DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        TODO("Not yet implemented")
    }
}