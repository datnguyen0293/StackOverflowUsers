package com.datnq.stack.overflow.users.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment(), BaseView {

    protected lateinit var mView: View

    @LayoutRes protected abstract fun getLayoutResourceId(): Int

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(getLayoutResourceId(), container, false)
        return mView
    }

    override fun onDestroyView() {
        activity().hideLoadingDialog()
        activity().hideErrorDialog()
        super.onDestroyView()
    }

}