package com.datnq.stack.overflow.users.application.view.adapter

import com.datnq.stack.overflow.users.R
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class ReputationAdapter : BaseRecyclerViewAdapter<Reputation?, ReputationViewHolder?>() {
    val layoutResourceId: Int
        get() = R.layout.layout_reputation_item

    fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ReputationViewHolder {
        return ReputationViewHolder(getView(viewGroup))
    }

    fun onBindViewHolder(holder: ReputationViewHolder, i: Int) {
        val data: Reputation = getItemAt(i)
        if (data != null) {
            holder.mTvChange.setText(
                java.lang.String.format(
                    Locale.getDefault(),
                    "Change: %d",
                    data.getReputationChange()
                )
            )
            holder.mTvCreateAt.setText(
                java.lang.String.format(
                    Locale.getDefault(),
                    "Create At: %s",
                    Utils.formatDate(data.getCreationDate())
                )
            )
            holder.mTvReputationType.setText(
                String.format(
                    Locale.getDefault(),
                    "Type: %s",
                    if (data.getReputationHistoryType() != null) data.getReputationHistoryType() else ""
                )
            )
            holder.mTvPostId.setText(
                java.lang.String.format(
                    Locale.getDefault(),
                    "Post ID: %d",
                    data.getPostId()
                )
            )
        }
    }
}