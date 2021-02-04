package com.datnq.stack.overflow.users.core

import dagger.android.support.DaggerFragment

abstract class BaseFragment: DaggerFragment(), BaseView {

    open fun onFragmentResume(){
        // For overriding
    }

    fun activity(): BaseActivity {
        return activity as BaseActivity
    }

    override fun showLoadingDialog(title: String, message: String) {
        activity().showLoadingDialog(title, message)
    }

    override fun showLoadingDialog(titleResourceId: Int, messageResourceId: Int) {
        activity().showLoadingDialog(titleResourceId, messageResourceId)
    }

    override fun hideLoadingDialog() {
        activity().hideLoadingDialog()
    }

    override fun showErrorDialog(errorMessage: String?, doOnError: Runnable?) {
        activity().showErrorDialog(errorMessage, doOnError)
    }

    override fun showErrorDialog(errorMessageStringResourceId: Int, doOnError: Runnable?) {
        activity().showErrorDialog(errorMessageStringResourceId, doOnError)
    }

    override fun showErrorDialog(errorMessage: String?) {
        activity().showErrorDialog(errorMessage)
    }

    override fun showErrorDialog(errorMessageStringResourceId: Int) {
        activity().showErrorDialog(errorMessageStringResourceId)
    }

    override fun onDestroyView() {
        activity().hideLoadingDialog()
        activity().hideErrorDialog()
        super.onDestroyView()
    }

}