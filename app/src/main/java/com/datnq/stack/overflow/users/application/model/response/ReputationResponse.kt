package com.datnq.stack.overflow.users.application.model.response

import com.datnq.stack.overflow.users.application.model.Reputation
import com.google.gson.annotations.SerializedName

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class ReputationResponse {
    @SerializedName("items")
    val listReputationItems: ArrayList<Reputation>? = null

    @SerializedName("has_more")
    private val mHasMore = false

    @SerializedName("quota_max")
    val quotaMax = 0

    @SerializedName("quota_remaining")
    private val mQuotaRemaining = 0
}