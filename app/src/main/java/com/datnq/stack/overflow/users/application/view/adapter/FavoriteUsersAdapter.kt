package com.datnq.stack.overflow.users.application.view.adapter

import android.view.View
import com.datnq.stack.overflow.users.R
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class FavoriteUsersAdapter : BaseRecyclerViewAdapter<UserItem?, UsersViewHolder?>() {
    private var mListener: UsersListener? = null
    fun setListener(listener: UsersListener?) {
        mListener = listener
    }

    private fun bindData(holder: UsersViewHolder, data: UserItem) {
        val bookmarked: Drawable =
            ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_favorite_on)
        holder.mBtnBookmark.setImageDrawable(bookmarked)
        holder.mImageAvatar.setImageBitmap(data.getUserAvatarBitmap())
        holder.mBtnBookmark.setImageDrawable(bookmarked)
        holder.mTvUserName.setText(if (data.getUserName() != null) data.getUserName() else "")
        holder.mTvLocation.setText(
            String.format(
                Locale.getDefault(),
                "Location: %s",
                if (data.getLocation() != null) data.getLocation() else ""
            )
        )
        holder.mTvLastAccessDate.setText(
            java.lang.String.format(
                Locale.getDefault(),
                "Last access date: %n%s",
                Utils.formatDate(data.getLastAccessDate())
            )
        )
        holder.mTvReputation.setText(
            java.lang.String.format(
                Locale.getDefault(),
                "Reputation: %d",
                data.getReputation()
            )
        )
    }

    val layoutResourceId: Int
        get() = R.layout.layout_user_item

    fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UsersViewHolder {
        return UsersViewHolder(getView(viewGroup))
    }

    fun onBindViewHolder(holder: UsersViewHolder, i: Int) {
        val data: UserItem = getItemAt(i)
        if (data != null) {
            bindData(holder, data)
            holder.mBtnBookmark.setOnClickListener(View.OnClickListener { v: View? ->
                if (mListener != null) {
                    mListener.saveAsFavorite(data)
                }
            })
        }
    }
}