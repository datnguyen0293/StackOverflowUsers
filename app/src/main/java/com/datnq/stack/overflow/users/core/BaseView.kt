package com.datnq.stack.overflow.users.core

import androidx.annotation.StringRes

interface BaseView {
    fun showLoadingDialog(title: String, message: String)
    fun showLoadingDialog(@StringRes titleResourceId: Int, @StringRes messageResourceId: Int)
    fun hideLoadingDialog()
    fun showErrorDialog(errorMessage: String?, doOnError: Runnable?)
    fun showErrorDialog(@StringRes errorMessageStringResourceId: Int, doOnError: Runnable?)
    fun showErrorDialog(errorMessage: String?)
    fun showErrorDialog(@StringRes errorMessageStringResourceId: Int)
}