package com.datnq.stack.overflow.users.application.view.adapter.viewholder

import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.datnq.stack.overflow.users.R

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class UsersViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
    @BindView(R.id.imageAvatar)
    var mImageAvatar: AppCompatImageView? = null

    @BindView(R.id.tvUserName)
    var mTvUserName: AppCompatTextView? = null

    @BindView(R.id.tvReputation)
    var mTvReputation: AppCompatTextView? = null

    @BindView(R.id.tvLocation)
    var mTvLocation: AppCompatTextView? = null

    @BindView(R.id.tvLastAccessDate)
    var mTvLastAccessDate: AppCompatTextView? = null

    @BindView(R.id.btnBookmark)
    var mBtnBookmark: AppCompatImageButton? = null
}