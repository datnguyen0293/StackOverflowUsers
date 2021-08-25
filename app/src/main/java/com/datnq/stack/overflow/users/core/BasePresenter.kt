package com.datnq.stack.overflow.users.core

import androidx.annotation.CallSuper
import com.datnq.stack.overflow.users.R
import com.gico.datnq.library.utilities.LoggerUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
abstract class BasePresenter<V : BaseView> {
    private var mView: V? = null

    companion object {
        const val STATUS_ERROR = "error"
        const val STATUS_OK = "ok"
    }

    protected fun view(): V? {
        return mView
    }

    @CallSuper
    fun bindView(view: V) {
        mView = view
    }

    @CallSuper
    fun unbindView() {
        CompositeDisposable().clear()
        mView = null
    }

    protected fun getErrorConsumer(): Consumer<Throwable>? {
        mView?.let { v ->
            return Consumer { throwable ->
                LoggerUtil.e(BasePresenter::class.java.simpleName, "getNetErrorConsumer()", throwable)
                v.showErrorDialog(if (Utilities.isOnline()) {
                    R.string.service_response_error_no_code
                } else {
                    R.string.error_network
                })
            }
        }
        return null
    }
}