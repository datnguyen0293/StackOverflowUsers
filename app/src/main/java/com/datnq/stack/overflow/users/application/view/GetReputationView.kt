package com.datnq.stack.overflow.users.application.view

import com.datnq.stack.overflow.users.application.model.Reputation
import com.datnq.stack.overflow.users.core.BaseView

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
interface GetReputationView : BaseView {
    fun onGetReputations(reputationList: List<Reputation?>?)
    fun onNoReputations()
}