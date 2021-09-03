package com.datnq.stack.overflow.users.application.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.presenter.FavoriteUsersPresenter
import com.datnq.stack.overflow.users.application.view.GetFavoriteUsersView
import com.datnq.stack.overflow.users.application.view.adapter.UsersAdapter
import com.datnq.stack.overflow.users.core.BaseFragment
import com.datnq.stack.overflow.users.databinding.FragmentFavoriteUsersBinding
import javax.inject.Inject

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class FavoriteUsersFragment : BaseFragment<FragmentFavoriteUsersBinding>(), GetFavoriteUsersView,
    UsersAdapter.UsersListener {

    @Inject lateinit var mAdapter: UsersAdapter
    @Inject lateinit var mPresenter: FavoriteUsersPresenter

    private fun initializeRecyclerView() {
        mAdapter.setListener(this)
        binding.rcvUsers.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity())
        binding.rcvUsers.layoutManager = linearLayoutManager
        binding.rcvUsers.adapter = mAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavoriteUsersBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
        mPresenter.bindView(this)
    }

    override fun onDestroyView() {
            mPresenter.unbindView()
        super.onDestroyView()
    }

    override fun goToDetail(userItem: UserItem) {
        // Don't go to detail
    }

    override fun saveAsFavorite(userItem: UserItem) {
        mPresenter.saveFavoriteUser(userItem)
    }

    override fun onGetFavoriteUsers(userItemList: ArrayList<UserItem>) {
            binding.tvNoData.visibility = View.GONE
            mAdapter.setData(userItemList)
    }

    override fun onNoFavoriteUsers() {
        binding.tvNoData.visibility = View.VISIBLE
    }

    override fun onSaveFavoriteUsers() {
        mPresenter.getFavoriteUsers()
    }
}