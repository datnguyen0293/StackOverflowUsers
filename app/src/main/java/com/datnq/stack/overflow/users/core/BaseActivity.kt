package com.datnq.stack.overflow.users.core

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.datnq.stack.overflow.users.hotel.GiHotelApplication
import com.datnq.stack.overflow.users.hotel.R
import kotlinx.android.synthetic.main.layout_loading.view.*
import java.util.*

abstract class BaseActivity : AppCompatActivity(), BaseView {

    private var mLoadingDialog: AlertDialog? = null
    private var mErrorDialog: AlertDialog? = null

    @CallSuper
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeLanguage(GiHotelApplication.sharedPreferencesCenter().getLanguage())
        if (getLayoutResourceId() != 0) {
            setContentView(getLayoutResourceId())
        }
    }

    @CallSuper
    override fun onDestroy() {
        hideLoadingDialog()
        hideErrorDialog()
        super.onDestroy()
    }

    override fun showLoadingDialog(title: String, message: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_loading, null, false)
        view.tvTitle.text = title
        view.tvMessage.text = message
        if (mLoadingDialog == null) {
            mLoadingDialog = AlertDialog.Builder(this)
                    .setCancelable(false)
                    .create()
        }
        mLoadingDialog?.setView(view)
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
                    .setIcon(R.drawable.app_icon3)
                    .setCancelable(true)
                    .setTitle(R.string.error)
                    .setPositiveButton(R.string.close) { dialog, _ ->
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

    @Suppress("DEPRECATION")
    private fun changeLanguage(languageCode: String) {
        val config = Configuration()
        config.fontScale = 1f
        config.locale = Locale(languageCode)
        resources.updateConfiguration(config, null)
        baseContext.resources.updateConfiguration(config, null)
        applicationContext.resources.updateConfiguration(config, null)
    }

    override fun startActivity(intent: Intent?) {
        overridePendingTransition(R.anim.fab_in, R.anim.fab_out)
        super.startActivity(intent)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        overridePendingTransition(R.anim.fab_in, R.anim.fab_out)
        super.startActivityForResult(intent, requestCode)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        overridePendingTransition(R.anim.fab_in, R.anim.fab_out)
        super.startActivityForResult(intent, requestCode, options)
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