package com.datnq.stack.overflow.users.application.view.adapter.viewholder

import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.datnq.stack.overflow.users.databinding.LayoutUserItemBinding

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class UsersViewHolder(binding: LayoutUserItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val mImageAvatar: AppCompatImageView = binding.imageAvatar
    val mTvUserName: AppCompatTextView = binding.tvUserName
    val mTvReputation: AppCompatTextView = binding.tvReputation
    val mTvLocation: AppCompatTextView = binding.tvLocation
    val mTvLastAccessDate: AppCompatTextView = binding.tvLastAccessDate
    val mBtnBookmark: AppCompatImageButton = binding.btnBookmark
}