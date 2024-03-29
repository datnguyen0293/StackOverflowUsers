package com.datnq.stack.overflow.users.di.module

import com.datnq.stack.overflow.users.application.presenter.AllUsersPresenter
import com.datnq.stack.overflow.users.application.presenter.service.ServiceCall
import com.datnq.stack.overflow.users.application.view.adapter.UsersAdapter
import com.datnq.stack.overflow.users.di.scopes.FragmentScope
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class AllUserFragmentModule {
    @Provides
    @FragmentScope
    fun provideAllUsersPresenter(
        services: ServiceCall,
        compositeDisposable: CompositeDisposable
    ): AllUsersPresenter {
        return AllUsersPresenter(services, compositeDisposable)
    }

    @Provides
    @FragmentScope
    fun provideUsersAdapter(): UsersAdapter {
        return UsersAdapter()
    }

}