package com.datnq.stack.overflow.users.application.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datnq.stack.overflow.users.application.model.Reputation
import com.datnq.stack.overflow.users.core.BaseRecyclerViewAdapter
import com.datnq.stack.overflow.users.core.Utilities
import com.datnq.stack.overflow.users.databinding.LayoutReputationItemBinding
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class ReputationAdapter : BaseRecyclerViewAdapter<Reputation, ReputationAdapter.ReputationViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ReputationViewHolder {
        return ReputationViewHolder(
            LayoutReputationItemBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReputationViewHolder, i: Int) {
        getItemAt(i)?.let {
            holder.binding.tvChange.text = String.format(
                Locale.getDefault(),
                "Change: %d",
                it.reputationChange
            )
            holder.binding.tvCreatedAt.text = String.format(
                Locale.getDefault(),
                "Create At: %s",
                Utilities.formatDate(it.creationDate)
            )
            holder.binding.tvReputationType.text = String.format(
                Locale.getDefault(),
                "Type: %s",
                it.reputationHistoryType
            )
            holder.binding.tvPostId.text = String.format(
                Locale.getDefault(),
                "Post ID: %d",
                it.postId
            )
        }
    }

    data class ReputationViewHolder(val binding: LayoutReputationItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}