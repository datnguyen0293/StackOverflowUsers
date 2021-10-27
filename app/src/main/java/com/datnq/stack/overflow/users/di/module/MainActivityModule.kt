package com.datnq.stack.overflow.users.di.module

import com.datnq.stack.overflow.users.application.presenter.FavoriteUsersPresenter
import com.datnq.stack.overflow.users.application.presenter.service.ServiceCall
import com.datnq.stack.overflow.users.application.view.fragment.AllUsersFragment
import com.datnq.stack.overflow.users.application.view.fragment.FavoriteUsersFragment
import com.datnq.stack.overflow.users.di.scopes.ActivityScope
import com.datnq.stack.overflow.users.di.scopes.FragmentScope
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.reactivex.disposables.CompositeDisposable

@Module
abstract class MainActivityModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [AllUserFragmentModule::class])
    abstract fun allUsersFragment(): AllUsersFragment?
    @FragmentScope
    @ContributesAndroidInjector(modules = [FavoriteUserFragmentModule::class])
    abstract fun favoriteUsersFragment(): FavoriteUsersFragment?
    companion object {
        @ActivityScope
        @Provides
        fun provideFavoriteUsersPresenter(
            services: ServiceCall,
            compositeDisposable: CompositeDisposable
        ): FavoriteUsersPresenter {
            return FavoriteUsersPresenter(services, compositeDisposable)
        }
    }
}