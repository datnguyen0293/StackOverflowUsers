package com.datnq.stack.overflow.users.application.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.view.adapter.viewholder.UsersViewHolder
import com.datnq.stack.overflow.users.application.view.listener.UsersListener
import com.datnq.stack.overflow.users.core.BaseRecyclerViewAdapter
import com.datnq.stack.overflow.users.core.Utilities
import com.datnq.stack.overflow.users.databinding.LayoutUserItemBinding
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class FavoriteUsersAdapter : BaseRecyclerViewAdapter<UserItem, UsersViewHolder>() {
    var listener: UsersListener? = null

    private fun bindData(holder: UsersViewHolder, data: UserItem) {
        holder.mBtnBookmark.setImageResource(R.drawable.ic_favorite_on)
        holder.mImageAvatar.setImageBitmap(data.userAvatarBitmap)
        holder.mTvUserName.text = data.userName
        holder.mTvLocation.text = String.format(
            Locale.getDefault(),
            "Location: %s",
            data.location
        )
        holder.mTvLastAccessDate.text = java.lang.String.format(
            Locale.getDefault(),
            "Last access date: %n%s",
            Utilities.formatDate(data.lastAccessDate)
        )
        holder.mTvReputation.text = java.lang.String.format(
            Locale.getDefault(),
            "Reputation: %d",
            data.reputation
        )
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UsersViewHolder {
        return UsersViewHolder(
            LayoutUserItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UsersViewHolder, i: Int) {
        getItemAt(i)?.let {
            bindData(holder, it)
            holder.mBtnBookmark.setOnClickListener {
                getItemAt(i)?.let { d -> listener?.saveAsFavorite(d) }
            }
        }
    }
}