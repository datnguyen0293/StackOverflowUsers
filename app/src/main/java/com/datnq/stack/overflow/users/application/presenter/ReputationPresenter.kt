package com.datnq.stack.overflow.users.application.presenter

import com.datnq.stack.overflow.users.application.model.Reputation
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class ReputationPresenter(services: ServiceCall, compositeDisposable: CompositeDisposable) :
    BasePresenter<GetReputationView?>() {
    fun getListReputation(
        userId: Long,
        page: Int,
        pageSize: Int,
        site: String?,
        creation: String?,
        sort: String?
    ) {
        Objects.requireNonNull(view()).showLoadingDialog()
        mDisposable = mServices.getListReputations(
            userId,
            page,
            pageSize,
            site,
            creation,
            sort,
            object : ApiCallback() {
                fun responseSucceed(obj: Any) {
                    if (!(obj as List<*>).isEmpty()) {
                        val reputationList: MutableList<Reputation> = ArrayList<Reputation>()
                        for (o in obj) {
                            reputationList.add(o as Reputation)
                        }
                        Objects.requireNonNull(view()).onGetReputations(reputationList)
                    } else {
                        Objects.requireNonNull(view()).onNoReputations()
                    }
                    Objects.requireNonNull(view()).hideLoadingDialog()
                }

                fun responseFail(errorMessage: String?) {
                    Objects.requireNonNull(view()).onNoReputations()
                    Objects.requireNonNull(view()).hideLoadingDialog()
                }

                fun callbackFail(throwable: Throwable?) {
                    Objects.requireNonNull(view()).onNoReputations()
                    Objects.requireNonNull(view()).hideLoadingDialog()
                    getNetErrorConsumer(throwable)
                }
            })
        mCompositeDisposable.add(mDisposable)
    }

    init {
        mServices = services
        mCompositeDisposable = compositeDisposable
    }
}