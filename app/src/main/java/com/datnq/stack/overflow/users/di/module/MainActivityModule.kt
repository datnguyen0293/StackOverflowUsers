package com.datnq.stack.overflow.users.di.module

import com.datnq.stack.overflow.users.application.view.adapter.UsersAdapter
import com.datnq.stack.overflow.users.application.view.fragment.AllUsersFragment
import com.datnq.stack.overflow.users.application.view.fragment.FavoriteUsersFragment
import com.datnq.stack.overflow.users.di.scopes.ActivityScope
import com.datnq.stack.overflow.users.di.scopes.FragmentScope
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [AllUserFragmentModule::class])
    abstract fun allUsersFragment(): AllUsersFragment?
    @FragmentScope
    @ContributesAndroidInjector(modules = [FavoriteUserFragmentModule::class])
    abstract fun favoriteUsersFragment(): FavoriteUsersFragment?

    companion object {

        @Provides
        @ActivityScope
        fun provideUsersAdapter(): UsersAdapter {
            return UsersAdapter()
        }
    }
}