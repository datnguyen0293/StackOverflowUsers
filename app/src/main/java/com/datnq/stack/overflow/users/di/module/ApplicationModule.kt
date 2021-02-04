package com.datnq.stack.overflow.users.di.module

import com.datnq.stack.overflow.users.GlobalApplication
import com.datnq.stack.overflow.users.application.presenter.service.ServiceApi
import com.datnq.stack.overflow.users.application.presenter.service.ServiceCall
import com.datnq.stack.overflow.users.application.presenter.service.ServiceFactory
import com.datnq.stack.overflow.users.database.SQLiteHelper
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideServiceApi(): ServiceApi {
        return ServiceFactory.create()
    }

    @Provides
    @Singleton
    fun provideSqLiteHelper(application: GlobalApplication): SQLiteHelper {
        return SQLiteHelper(application)
    }

    @Provides
    @Singleton
    fun provideServiceCall(serviceApi: ServiceApi, sqLiteHelper: SQLiteHelper): ServiceCall {
        return ServiceCall(serviceApi, sqLiteHelper)
    }

    @Provides
    @Singleton
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}