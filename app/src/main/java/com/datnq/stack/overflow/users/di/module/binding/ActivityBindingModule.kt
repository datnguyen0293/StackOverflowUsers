package com.datnq.stack.overflow.users.di.module.binding

import com.datnq.stack.overflow.users.application.view.activity.MainActivity
import com.datnq.stack.overflow.users.application.view.activity.ReputationActivity
import com.datnq.stack.overflow.users.di.module.MainActivityModule
import com.datnq.stack.overflow.users.di.module.ReputaionActivityModule
import com.datnq.stack.overflow.users.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivity(): MainActivity
    @ActivityScope
    @ContributesAndroidInjector(modules = [ReputaionActivityModule::class])
    abstract fun reputationActivity(): ReputationActivity
}