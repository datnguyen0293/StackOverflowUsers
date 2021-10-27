package com.datnq.stack.overflow.users.application.presenter

import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.presenter.service.ServiceCall
import com.datnq.stack.overflow.users.application.view.GetFavoriteUsersView
import com.datnq.stack.overflow.users.core.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class FavoriteUsersPresenter(
    private val services: ServiceCall,
    private val compositeDisposable: CompositeDisposable
) :
    BasePresenter<GetFavoriteUsersView>() {
    fun getFavoriteUsers() {
        view()?.showLoadingDialog(R.string.load_data, R.string.processing)
        compositeDisposable.add(services.getFavoriteUsers(object : ServiceCall.ApiCallback {
            override fun responseSucceed(obj: Any?) {
                val userItemList: ArrayList<UserItem> = ArrayList<UserItem>()
                for (o in obj as ArrayList<*>) {
                    userItemList.add(o as UserItem)
                }
                if (userItemList.isEmpty()) {
                    view()?.onNoFavoriteUsers()
                } else {
                    view()?.onGetFavoriteUsers(userItemList)
                }
                view()?.hideLoadingDialog()
            }

            override fun responseFail(errorMessage: String?) {
                view()?.onNoFavoriteUsers()
                view()?.hideLoadingDialog()
            }

            override fun callbackFail(throwable: Throwable?) {
                view()?.onNoFavoriteUsers()
                getErrorConsumer()
                view()?.hideLoadingDialog()
            }
        }))
    }

    fun saveFavoriteUser(userItem: UserItem) {
        view()?.showLoadingDialog(R.string.load_data, R.string.processing)
        compositeDisposable.add(
            services.saveFavoriteUser(
                userItem,
                object : ServiceCall.ApiCallback {
                    override fun responseSucceed(obj: Any?) {
                        view()?.onSaveFavoriteUsers()
                        view()?.hideLoadingDialog()
                    }

                    override fun responseFail(errorMessage: String?) {
                        // Do nothing
                    }

                    override fun callbackFail(throwable: Throwable?) {
                        getErrorConsumer()
                        view()?.hideLoadingDialog()
                    }
                })
        )
    }

}