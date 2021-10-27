package com.datnq.stack.overflow.users.application.presenter

import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.Reputation
import com.datnq.stack.overflow.users.application.presenter.service.ServiceCall
import com.datnq.stack.overflow.users.application.view.GetReputationView
import com.datnq.stack.overflow.users.core.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class ReputationPresenter(
    private val services: ServiceCall,
    private val compositeDisposable: CompositeDisposable
) :
    BasePresenter<GetReputationView>() {
    fun getListReputation(
        userId: Long,
        page: Int,
        pageSize: Int,
        site: String?,
        creation: String?,
        sort: String?
    ) {
        view()?.showLoadingDialog(R.string.load_data, R.string.processing)
        compositeDisposable.add(
            services.getListReputations(
                userId,
                page,
                pageSize,
                site,
                creation,
                sort,
                object : ServiceCall.ApiCallback {
                    override fun responseSucceed(obj: Any?) {
                        if ((obj as ArrayList<*>).isNotEmpty()) {
                            val reputationList: ArrayList<Reputation> = ArrayList<Reputation>()
                            for (o in obj) {
                                reputationList.add(o as Reputation)
                            }
                            view()?.onGetReputations(reputationList)
                        } else {
                            view()?.onNoReputations()
                        }
                        view()?.hideLoadingDialog()
                    }

                    override fun responseFail(errorMessage: String?) {
                        view()?.onNoReputations()
                        view()?.hideLoadingDialog()
                    }

                    override fun callbackFail(throwable: Throwable?) {
                        view()?.onNoReputations()
                        view()?.hideLoadingDialog()
                        getErrorConsumer()
                    }
                })
        )
    }
}