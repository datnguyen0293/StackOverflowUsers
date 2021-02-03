package com.datnq.stack.overflow.users.application.presenter

import com.datnq.stack.overflow.users.application.model.UserItem
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class FavoriteUsersPresenter(services: ServiceCall, compositeDisposable: CompositeDisposable) :
    BasePresenter<GetFavoriteUsersView?>() {
    val favoriteUsers: Unit
        get() {
            Objects.requireNonNull(view()).showLoadingDialog()
            mDisposable = mServices.getFavoriteUsers(object : ApiCallback() {
                fun responseSucceed(obj: Any) {
                    val userItemList: MutableList<UserItem> = ArrayList<UserItem>()
                    for (o in obj as List<*>) {
                        userItemList.add(o as UserItem)
                    }
                    if (userItemList.isEmpty()) {
                        Objects.requireNonNull(view()).onNoFavoriteUsers()
                    } else {
                        Objects.requireNonNull(view()).onGetFavoriteUsers(userItemList)
                    }
                    Objects.requireNonNull(view()).hideLoadingDialog()
                }

                fun responseFail(errorMessage: String?) {
                    Objects.requireNonNull(view()).onNoFavoriteUsers()
                    Objects.requireNonNull(view()).hideLoadingDialog()
                }

                fun callbackFail(throwable: Throwable?) {
                    Objects.requireNonNull(view()).onNoFavoriteUsers()
                    getNetErrorConsumer(throwable)
                    Objects.requireNonNull(view()).hideLoadingDialog()
                }
            })
            mCompositeDisposable.add(mDisposable)
        }

    fun saveFavoriteUser(userItem: UserItem?) {
        Objects.requireNonNull(view()).showLoadingDialog()
        mDisposable = mServices.saveFavoriteUser(userItem, object : ApiCallback() {
            fun responseSucceed(obj: Any?) {
                Objects.requireNonNull(view()).onSaveFavoriteUsers()
                Objects.requireNonNull(view()).hideLoadingDialog()
            }

            fun responseFail(errorMessage: String?) {
                // Do nothing
            }

            fun callbackFail(throwable: Throwable?) {
                getNetErrorConsumer(throwable)
                Objects.requireNonNull(view()).hideLoadingDialog()
            }
        })
        mCompositeDisposable.add(mDisposable)
    }

    init {
        mServices = services
        mCompositeDisposable = compositeDisposable
    }
}