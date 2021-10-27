package com.datnq.stack.overflow.users.application.presenter

import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.presenter.service.ServiceCall
import com.datnq.stack.overflow.users.application.view.GetAllUsersView
import com.datnq.stack.overflow.users.core.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class AllUsersPresenter(private val services: ServiceCall, private val compositeDisposable: CompositeDisposable) :
    BasePresenter<GetAllUsersView>() {
    fun getListUser(page: Int, pageSize: Int, site: String?, creation: String?, sort: String?) {
        view()?.showLoadingDialog(R.string.load_data, R.string.processing)
        compositeDisposable.add(
            services.getListUsers(
                page,
                pageSize,
                site,
                creation,
                sort,
                object : ServiceCall.ApiCallback {
                    override fun responseSucceed(obj: Any?) {
                        if ((obj as ArrayList<*>).isNotEmpty()) {
                            val userItemList: ArrayList<UserItem> = ArrayList<UserItem>()
                            for (o in obj) {
                                userItemList.add(o as UserItem)
                            }
                            view()?.onGetAllUsers(userItemList)
                        } else {
                            view()?.onNoUsers()
                        }
                        view()?.hideLoadingDialog()
                    }

                    override fun responseFail(errorMessage: String?) {
                        view()?.onNoUsers()
                        view()?.hideLoadingDialog()
                    }

                    override fun callbackFail(throwable: Throwable?) {
                        view()?.onNoUsers()
                        view()?.hideLoadingDialog()
                        getErrorConsumer()
                    }
                })
        )
    }

}