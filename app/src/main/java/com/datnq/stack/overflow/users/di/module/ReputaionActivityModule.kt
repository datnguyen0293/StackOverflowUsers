package com.datnq.stack.overflow.users.di.module

import androidx.recyclerview.widget.LinearLayoutManager
import com.datnq.stack.overflow.users.GlobalApplication
import com.datnq.stack.overflow.users.application.presenter.ReputationPresenter
import com.datnq.stack.overflow.users.application.presenter.service.ServiceCall
import com.datnq.stack.overflow.users.application.view.adapter.ReputationAdapter
import com.datnq.stack.overflow.users.di.scopes.ActivityScope
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ReputaionActivityModule {
    @Provides
    @ActivityScope
    fun provideReputationPresenter(
        services: ServiceCall,
        compositeDisposable: CompositeDisposable
    ): ReputationPresenter {
        return ReputationPresenter(services, compositeDisposable)
    }

    @Provides
    @ActivityScope
    fun provideLinearLayoutManager(application: GlobalApplication): LinearLayoutManager {
        return LinearLayoutManager(application)
    }

    @Provides
    @ActivityScope
    fun provideReputationAdapter(): ReputationAdapter {
        return ReputationAdapter()
    }
}