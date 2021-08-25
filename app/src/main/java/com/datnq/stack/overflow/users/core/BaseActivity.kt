package com.datnq.stack.overflow.users.core

import android.content.Intent
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.databinding.LayoutLoadingBinding
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity(), BaseView {

    private var mLoadingDialog: AlertDialog? = null
    private var mErrorDialog: AlertDialog? = null

    @CallSuper
    override fun onDestroy() {
        hideLoadingDialog()
        hideErrorDialog()
        super.onDestroy()
    }

    override fun showLoadingDialog(title: String, message: String) {
        val view = LayoutLoadingBinding.inflate(layoutInflater)
        view.tvTitle.text = title
        view.tvMessage.text = message
        if (mLoadingDialog == null) {
            mLoadingDialog = AlertDialog.Builder(this)
                    .setCancelable(false)
                    .create()
        }
        mLoadingDialog?.setView(view.root)
        mLoadingDialog?.setCanceledOnTouchOutside(false)
        mLoadingDialog?.isShowing?.let { isShowing ->
            if (!isShowing && !isFinishing) {
                mLoadingDialog?.show()
            }
        }
    }

    override fun showLoadingDialog(titleResourceId: Int, messageResourceId: Int) {
        showLoadingDialog(getString(titleResourceId), getString(messageResourceId))
    }

    override fun hideLoadingDialog() {
        mLoadingDialog?.isShowing?.let { isShowing ->
            if (isShowing) {
                mLoadingDialog?.dismiss()
            }
        }
        mLoadingDialog = null
    }

    override fun showErrorDialog(errorMessage: String?, doOnError: Runnable?) {
        if (mErrorDialog == null) {
            mErrorDialog = AlertDialog.Builder(this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.logo)
                    .setCancelable(true)
                    .setTitle(R.string.service_response_error_no_code)
                    .setPositiveButton(R.string.action_close) { dialog, _ ->
                        doOnError?.run()
                        dialog.dismiss()
                    }.create()
        }
        mErrorDialog?.setMessage(errorMessage)
        if (!isFinishing) {
            mErrorDialog?.show()
        }
    }

    override fun showErrorDialog(errorMessageStringResourceId: Int, doOnError: Runnable?) {
        showErrorDialog(getString(errorMessageStringResourceId), doOnError)
    }

    override fun showErrorDialog(errorMessage: String?) {
        showErrorDialog(errorMessage, null)
    }

    override fun showErrorDialog(errorMessageStringResourceId: Int) {
        showErrorDialog(errorMessageStringResourceId, null)
    }

    open fun hideErrorDialog() {
        mErrorDialog?.let { dialog ->
            dialog.dismiss()
            mErrorDialog = null
        }
    }

    override fun startActivity(intent: Intent?) {
        overridePendingTransition(R.anim.fab_in, R.anim.fab_out)
        super.startActivity(intent)
    }

    override fun finish() {
        overridePendingTransition(R.anim.fab_in, R.anim.fab_out)
        super.finish()
    }

    override fun finishAndRemoveTask() {
        overridePendingTransition(R.anim.fab_in, R.anim.fab_out)
        super.finishAndRemoveTask()
    }

}