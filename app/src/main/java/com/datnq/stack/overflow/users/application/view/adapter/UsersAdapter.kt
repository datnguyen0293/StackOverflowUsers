package com.datnq.stack.overflow.users.application.view.adapter

import android.view.View
import com.datnq.stack.overflow.users.R
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class UsersAdapter : BaseRecyclerViewAdapter<UserItem?, UsersViewHolder?>() {
    private var mListener: UsersListener? = null
    private var mListFavoriteUsers: MutableList<UserItem>? = null
    fun setListener(mListener: UsersListener?) {
        this.mListener = mListener
    }

    fun setListFavoriteUsers(listFavoriteUsers: List<UserItem>?) {
        if (mListFavoriteUsers == null) {
            mListFavoriteUsers = ArrayList<UserItem>()
        } else {
            mListFavoriteUsers!!.clear()
        }
        mListFavoriteUsers!!.addAll(listFavoriteUsers!!)
        notifyDataSetChanged()
    }

    private fun isFavorite(userItem: UserItem): Boolean {
        if (mListFavoriteUsers != null && !mListFavoriteUsers!!.isEmpty()) {
            for (user in mListFavoriteUsers!!) {
                if (user.getUserId() === userItem.getUserId()) {
                    return true
                }
            }
        }
        return false
    }

    private fun bindData(holder: UsersViewHolder, data: UserItem) {
        val bookmarked: Drawable =
            ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_favorite_on)
        val unBookmarked: Drawable =
            ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_favorite)
        holder.mBtnBookmark.setImageDrawable(unBookmarked)
        Picasso.Builder(holder.itemView.getContext()).build().load(data.getUserAvatar())
            .memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(holder.mImageAvatar)
        holder.mBtnBookmark.setImageDrawable(if (isFavorite(data)) bookmarked else unBookmarked)
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
            holder.itemView.setOnClickListener { v ->
                if (mListener != null) {
                    mListener.goToDetail(data)
                }
            }
            holder.mBtnBookmark.setOnClickListener(View.OnClickListener { v: View? ->
                if (mListener != null) {
                    mListener.saveAsFavorite(data)
                }
            })
        }
    }
}