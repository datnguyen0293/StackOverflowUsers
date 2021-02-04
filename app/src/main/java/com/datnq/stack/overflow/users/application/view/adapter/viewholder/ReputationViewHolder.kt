package com.datnq.stack.overflow.users.application.view.adapter.viewholder

import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.datnq.stack.overflow.users.databinding.LayoutReputationItemBinding

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class ReputationViewHolder(binding: LayoutReputationItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val mTvReputationType: AppCompatTextView = binding.tvReputationType
    val mTvChange: AppCompatTextView = binding.tvChange
    val mTvCreateAt: AppCompatTextView = binding.tvCreatedAt
    val mTvPostId: AppCompatTextView = binding.tvPostId
}