package com.datnq.stack.overflow.users.application.presenter

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
        view()?.showLoadingDialog()
        compositeDisposable.add(services.getListUsers(page, pageSize, site, creation, sort, object : ServiceCall.ApiCallback {
            override fun responseSucceed(obj: Any?) {
                TODO("Not yet implemented")
            }

            override fun responseFail(errorMessage: String?) {
                TODO("Not yet implemented")
            }

            override fun callbackFail(throwable: Throwable?) {
                TODO("Not yet implemented")
            }
//                fun responseSucceed(obj: Any?) {
//                    if (!(obj as List<*>).isEmpty()) {
//                        val userItemList: MutableList<UserItem> = ArrayList<UserItem>()
//                        for (o in obj) {
//                            userItemList.add(o as UserItem)
//                        }
//                        Objects.requireNonNull(view()).onGetAllUsers(userItemList)
//                    } else {
//                        Objects.requireNonNull(view()).onNoUsers()
//                    }
//                    Objects.requireNonNull(view()).hideLoadingDialog()
//                }
//
//            fun responseFail(errorMessage: String?) {
//                    Objects.requireNonNull(view()).onNoUsers()
//                    Objects.requireNonNull(view()).hideLoadingDialog()
//                }
//
//                fun callbackFail(throwable: Throwable?) {
//                    Objects.requireNonNull(view()).onNoUsers()
//                    Objects.requireNonNull(view()).hideLoadingDialog()
//                    getNetErrorConsumer(throwable)
//                }
//            }))
        }))
    }

}