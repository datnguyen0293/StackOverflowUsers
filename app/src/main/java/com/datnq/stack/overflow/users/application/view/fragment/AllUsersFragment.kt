package com.datnq.stack.overflow.users.application.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.presenter.AllUsersPresenter
import com.datnq.stack.overflow.users.application.presenter.FavoriteUsersPresenter
import com.datnq.stack.overflow.users.application.view.GetAllUsersView
import com.datnq.stack.overflow.users.application.view.GetFavoriteUsersView
import com.datnq.stack.overflow.users.application.view.activity.ReputationActivity
import com.datnq.stack.overflow.users.application.view.adapter.UsersAdapter
import com.datnq.stack.overflow.users.core.BaseFragment
import com.datnq.stack.overflow.users.core.RecyclerViewScrollEvent
import com.datnq.stack.overflow.users.databinding.FragmentAllUsersBinding
import java.util.*
import javax.inject.Inject

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class AllUsersFragment : BaseFragment<FragmentAllUsersBinding>(), GetAllUsersView, GetFavoriteUsersView,
    UsersAdapter.UsersListener {

    @Inject lateinit var mAllUserPresenter: AllUsersPresenter
    @Inject lateinit var mFavoriteUsersPresenter: FavoriteUsersPresenter
    @Inject lateinit var mAdapter: UsersAdapter
    private var mTotalItems: Long = 0
    private var mPage = 1

    /**
     * Initialize RecyclerView
     */
    private fun initializeRecyclerView() {
        mAdapter.setListener(this)
        binding.rcvUsers.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity())
        binding.rcvUsers.layoutManager = linearLayoutManager
        binding.rcvUsers.adapter = mAdapter
        // Scroll to load more data
        binding.rcvUsers.addOnScrollListener(object : RecyclerViewScrollEvent(linearLayoutManager) {
            override fun loadMoreItems() {
                mTotalItems += PAGE_SIZE.toLong()
                mPage++
                mAllUserPresenter.getListUser(
                    mPage,
                    PAGE_SIZE,
                    getString(R.string.stack_overflow_site),
                    getString(R.string.sort_by),
                    getString(R.string.order_by)
                )
            }

            override fun isLastPage(currentTotalItemCount: Long): Boolean {
                return mTotalItems == currentTotalItemCount
            }

            override fun isLoading(): Boolean {
                return false
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAllUsersBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
        onFragmentResume()
    }

    override fun onFragmentResume() {
        mAllUserPresenter.bindView(this)
        mFavoriteUsersPresenter.bindView(this)
        mAllUserPresenter.getListUser(
            mPage,
            PAGE_SIZE,
            getString(R.string.stack_overflow_site),
            getString(R.string.sort_by),
            getString(R.string.order_by)
        )
    }

    override fun onDestroyView() {
            mAllUserPresenter.unbindView()
            mFavoriteUsersPresenter.unbindView()
        super.onDestroyView()
    }

    override fun onGetAllUsers(userItemList: ArrayList<UserItem>) {
            binding.tvNoData.visibility = View.GONE
            mAdapter.setListener(this)
            mAdapter.setData(userItemList)
            mFavoriteUsersPresenter.getFavoriteUsers()
    }

    override fun onNoFavoriteUsers() {
        mAdapter.setListFavoriteUsers(ArrayList<UserItem>())
        binding.tvNoData.visibility = View.GONE
    }

    override fun onNoUsers() {
        binding.tvNoData.visibility = View.VISIBLE
    }

    override fun onGetFavoriteUsers(userItemList: ArrayList<UserItem>) {
        mAdapter.setListFavoriteUsers(userItemList)
    }

    override fun goToDetail(userItem: UserItem) {
        ReputationActivity.start(this, userItem)
    }

    override fun saveAsFavorite(userItem: UserItem) {
        mFavoriteUsersPresenter.saveFavoriteUser(userItem)
    }

    override fun onSaveFavoriteUsers() {
        mFavoriteUsersPresenter.getFavoriteUsers()
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}