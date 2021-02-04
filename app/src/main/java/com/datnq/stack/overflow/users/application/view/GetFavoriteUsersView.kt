package com.datnq.stack.overflow.users.application.view

import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.core.BaseView

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
interface GetFavoriteUsersView : BaseView {
    fun onGetFavoriteUsers(userItemList: ArrayList<UserItem>)
    fun onNoFavoriteUsers()
    fun onSaveFavoriteUsers()
}