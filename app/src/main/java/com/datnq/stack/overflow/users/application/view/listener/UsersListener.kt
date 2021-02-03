package com.datnq.stack.overflow.users.application.view.listener

import com.datnq.stack.overflow.users.application.model.UserItem

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
interface UsersListener {
    fun goToDetail(userItem: UserItem)
    fun saveAsFavorite(userItem: UserItem)
}