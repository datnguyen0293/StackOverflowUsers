package com.datnq.stack.overflow.users.application.view.adapter.viewholder

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.datnq.stack.overflow.users.R

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class ReputationViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
    @BindView(R.id.tvReputationType)
    var mTvReputationType: AppCompatTextView? = null

    @BindView(R.id.tvChange)
    var mTvChange: AppCompatTextView? = null

    @BindView(R.id.tvCreatedAt)
    var mTvCreateAt: AppCompatTextView? = null

    @BindView(R.id.tvPostId)
    var mTvPostId: AppCompatTextView? = null
}