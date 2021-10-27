package com.datnq.stack.overflow.users.application.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.view.fragment.AllUsersFragment
import com.datnq.stack.overflow.users.core.BaseRecyclerViewAdapter
import com.datnq.stack.overflow.users.core.Utilities
import com.datnq.stack.overflow.users.databinding.LayoutUserItemBinding
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class UsersAdapter : BaseRecyclerViewAdapter<UserItem, UsersAdapter.UsersViewHolder>() {
    private var listener: UsersListener? = null
    private var listFavoriteUsers: ArrayList<UserItem> = ArrayList()
    private var screenName = ""

    fun setScreenName(name: String) {
        screenName = name
        notifyDataSetChanged()
    }

    fun setListFavoriteUsers(list: ArrayList<UserItem>) {
        listFavoriteUsers.clear()
        listFavoriteUsers.addAll(list)
        notifyDataSetChanged()
    }

    fun setListener(mListener: UsersListener?) {
        this.listener = mListener
    }

    private fun isFavorite(userItem: UserItem): Boolean {
        if (listFavoriteUsers.isNotEmpty()) {
            for (user in listFavoriteUsers) {
                if (user.userId == userItem.userId) {
                    return true
                }
            }
        }
        return false
    }

    private fun bindData(holder: UsersViewHolder, data: UserItem) {
        holder.binding.btnBookmark.setImageResource(R.drawable.ic_favorite)
        if (AllUsersFragment::class.java.simpleName.equals(screenName, true)) {
            Picasso.Builder(holder.itemView.context).build().load(data.userAvatar)
                .fit().into(holder.binding.imageAvatar)
        } else {
            holder.binding.imageAvatar.setImageBitmap(data.userAvatarBitmap)
        }
        holder.binding.btnBookmark.setImageResource(if (isFavorite(data)) R.drawable.ic_favorite_on else R.drawable.ic_favorite)
        holder.binding.tvUserName.text = data.userName
        holder.binding.tvLocation.text = String.format(
            Locale.getDefault(),
            "Location: %s",
            data.location
        )
        holder.binding.tvLastAccessDate.text = java.lang.String.format(
            Locale.getDefault(),
            "Last access date: %n%s",
            Utilities.formatDate(data.lastAccessDate)
        )
        holder.binding.tvLocation.text = java.lang.String.format(
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
        }
        holder.itemView.setOnClickListener {
            getItemAt(i)?.let { d -> listener?.goToDetail(d) }
        }
        holder.binding.btnBookmark.setOnClickListener {
            getItemAt(i)?.let { d -> listener?.saveAsFavorite(d) }
        }
    }

    data class UsersViewHolder(val binding: LayoutUserItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface UsersListener {
        fun goToDetail(userItem: UserItem)
        fun saveAsFavorite(userItem: UserItem)
    }
}