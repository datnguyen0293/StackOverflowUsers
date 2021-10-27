package com.datnq.stack.overflow.users.application.presenter.service

import com.datnq.stack.overflow.users.BuildConfig
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.model.response.ReputationResponse
import com.datnq.stack.overflow.users.application.model.response.UsersResponse
import com.datnq.stack.overflow.users.core.LoggerUtil
import com.datnq.stack.overflow.users.database.SQLiteHelper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class ServiceCall(private val mServiceApi: ServiceApi, private val sqLiteHelper: SQLiteHelper) {

    interface ApiCallback {
        fun responseSucceed(obj: Any?)
        fun responseFail(errorMessage: String?)
        fun callbackFail(throwable: Throwable?)
    }

    fun getListUsers(
        page: Int,
        pageSize: Int,
        site: String?,
        creation: String?,
        sort: String?,
        callback: ApiCallback
    ): Disposable {
        return mServiceApi.getUsers(
            page,
            pageSize,
            site,
            creation,
            sort,
            BuildConfig.STACK_OVERFLOW_APPLICATION_ID
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ usersResponse: UsersResponse? ->
                usersResponse?.listUserItems?.let {
                    if (it.isNotEmpty()) {
                        callback.responseSucceed(it)
                    } else {
                        callback.responseFail(null)
                    }
                }
            }, { throwable: Throwable? ->
                throwable?.let { e -> callback.callbackFail(e) }
            })
    }

    fun getListReputations(
        userId: Long,
        page: Int,
        pageSize: Int,
        site: String?,
        creation: String?,
        sort: String?,
        callback: ApiCallback
    ): Disposable {
        return mServiceApi.getReputations(
            userId,
            page,
            pageSize,
            site,
            creation,
            sort,
            BuildConfig.STACK_OVERFLOW_APPLICATION_ID
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ usersResponse: ReputationResponse? ->
                if (usersResponse != null) {
                    if (usersResponse.listReputationItems != null) {
                        callback.responseSucceed(usersResponse.listReputationItems)
                    } else {
                        callback.responseFail(null)
                    }
                }
            }, { throwable: Throwable? ->
                throwable?.let { e -> callback.callbackFail(e) }
            })
    }

    fun getFavoriteUsers(callback: ApiCallback): Disposable {
        return Single.defer(Callable<SingleSource<ArrayList<UserItem>>> {
            Single.just(
                sqLiteHelper.getFavoriteUsers()
            )
        } as Callable<SingleSource<ArrayList<UserItem>>>?)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ listUsers: ArrayList<UserItem> ->
                callback.responseSucceed(listUsers)
            }) { throwable: Throwable? ->
                throwable?.let { e -> callback.callbackFail(e) }
            }
    }

    fun saveFavoriteUser(userItem: UserItem, callback: ApiCallback): Disposable {
        return Completable.fromRunnable {
            try {
                sqLiteHelper.updateFavoriteUsers(userItem)
            } catch (e: Exception) {
                LoggerUtil.e(
                    ServiceCall::class.java.simpleName,
                    "saveFavoriteUser(UserItem userItem)",
                    e
                )
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { callback.responseSucceed(null) },
                { throwable: Throwable? ->
                    throwable?.let { e -> callback.callbackFail(e) }
                })
    }
}