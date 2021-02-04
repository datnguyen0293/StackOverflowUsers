package com.datnq.stack.overflow.users.di.components

import com.datnq.stack.overflow.users.GlobalApplication
import com.datnq.stack.overflow.users.di.module.ApplicationModule
import com.datnq.stack.overflow.users.di.module.binding.ActivityBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ActivityBindingModule::class, ApplicationModule::class])
interface ApplicationComponent : AndroidInjector<GlobalApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: GlobalApplication): Builder
        fun build(): ApplicationComponent
    }
}