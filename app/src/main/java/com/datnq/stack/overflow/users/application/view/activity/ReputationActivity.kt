package com.datnq.stack.overflow.users.application.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.Reputation
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.application.presenter.ReputationPresenter
import com.datnq.stack.overflow.users.application.view.GetReputationView
import com.datnq.stack.overflow.users.application.view.adapter.ReputationAdapter
import com.datnq.stack.overflow.users.core.BaseActivity
import com.datnq.stack.overflow.users.core.BaseFragment
import com.datnq.stack.overflow.users.core.RecyclerViewScrollEvent
import com.datnq.stack.overflow.users.core.Utilities
import com.datnq.stack.overflow.users.databinding.ActivityReputationBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.stone.vega.library.VegaLayoutManager
import java.util.*
import javax.inject.Inject

class ReputationActivity : BaseActivity<ActivityReputationBinding>(), GetReputationView {

    @Inject
    lateinit var mPresenter: ReputationPresenter

    @Inject
    lateinit var mAdapter: ReputationAdapter

    lateinit var mLinearLayoutManager: VegaLayoutManager
    private var mUserItem: UserItem? = null
    private var mTotalItems: Long = 0
    private var mPage = 1

    companion object {
        private const val PAGE_SIZE = 30
        private const val ARG_USER_ITEM = "ARG_USER_ITEM"

        @JvmStatic
        fun start(baseFragment: BaseFragment<*>, userItem: UserItem) {
            val starter = Intent(baseFragment.activity(), ReputationActivity::class.java)
            starter.putExtra(ARG_USER_ITEM, userItem)
            baseFragment.startActivity(starter)
        }
    }

    /**
     * Display user's information
     * @param data The user's information
     */
    private fun displayUserInformation(data: UserItem) {
        Picasso.Builder(this).build().load(data.userAvatar).memoryPolicy(MemoryPolicy.NO_CACHE)
            .fit().into(binding.layoutUser.imageAvatar)
        binding.layoutUser.tvUserName.text = data.userName
        binding.layoutUser.tvLocation.text = String.format(
            Locale.getDefault(),
            "Location: %s",
            data.location
        )
        binding.layoutUser.tvLastAccessDate.text = String.format(
            Locale.getDefault(),
            "Last access date: %n%s",
            Utilities.formatDate(data.lastAccessDate)
        )
        binding.layoutUser.tvReputation.text =
            String.format(Locale.getDefault(), "Reputation: %d", data.reputation)
    }

    /**
     * Initialize RecyclerView
     */
    private fun initializeRecyclerView() {
        mLinearLayoutManager = VegaLayoutManager()
        binding.rcvReputation.setHasFixedSize(true)
        binding.rcvReputation.layoutManager = mLinearLayoutManager
        binding.rcvReputation.adapter = mAdapter
        // Scroll to load more data
        binding.rcvReputation.addOnScrollListener(object :
            RecyclerViewScrollEvent(mLinearLayoutManager) {
            override fun loadMoreItems() {
                mUserItem?.let {
                    mTotalItems += PAGE_SIZE.toLong()
                    mPage++
                    mPresenter.getListReputation(
                        it.userId,
                        mPage,
                        PAGE_SIZE,
                        getString(R.string.stack_overflow_site),
                        getString(R.string.sort_by),
                        getString(R.string.order_by)
                    )
                }
            }

            override fun isLastPage(currentTotalItemCount: Long): Boolean {
                return mTotalItems == currentTotalItemCount
            }

            override fun isLoading(): Boolean {
                return false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityReputationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding.layoutUser.btnBookmark.visibility = View.GONE
        initializeRecyclerView()
        mPresenter.bindView(this)
        mUserItem = intent.getParcelableExtra(ARG_USER_ITEM)
        mUserItem?.let {
            displayUserInformation(it)
            mPresenter.getListReputation(
                it.userId,
                mPage,
                PAGE_SIZE,
                getString(R.string.stack_overflow_site),
                getString(R.string.sort_by),
                getString(R.string.order_by)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.unbindView()
    }

    override fun onGetReputations(reputationList: ArrayList<Reputation>) {
        binding.tvNoData.visibility = View.GONE
        mAdapter.setData(reputationList)
    }

    override fun onNoReputations() {
        binding.tvNoData.visibility = View.VISIBLE
    }

}