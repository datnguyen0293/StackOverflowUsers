package com.datnq.stack.overflow.users.application.view.fragment

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.datnq.stack.overflow.users.R
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class AllUsersFragment : BaseFragment(R.layout.fragment_all_users), GetAllUsersView,
    GetFavoriteUsersView, UsersListener {
    @BindView(R.id.rcvUsers)
    var mRcvUsers: RecyclerView? = null

    @BindView(R.id.tvNoData)
    var mTvNoData: AppCompatTextView? = null

    @Inject
    var mAllUserPresenter: AllUsersPresenter? = null

    @Inject
    var mFavoriteUsersPresenter: FavoriteUsersPresenter? = null

    @Inject
    var mAdapter: UsersAdapter? = null
    private var mTotalItems: Long = 0
    private var mPage = 1

    /**
     * Initialize RecyclerView
     */
    private fun initializeRecyclerView() {
        mAdapter.setListener(this)
        mRcvUsers.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.activity())
        mRcvUsers.setLayoutManager(linearLayoutManager)
        mRcvUsers.setAdapter(mAdapter)
        // Scroll to load more data
        mRcvUsers.addOnScrollListener(object : RecyclerViewScrollEvent(linearLayoutManager) {
            protected fun loadMoreItems() {
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

            fun isLastPage(currentTotalItemCount: Long): Boolean {
                return mTotalItems == currentTotalItemCount
            }

            val isLoading: Boolean
                get() = false
        })
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
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

    fun onDestroyView() {
        if (mAllUserPresenter != null) {
            mAllUserPresenter.unbindView()
            mAllUserPresenter = null
        }
        if (mFavoriteUsersPresenter != null) {
            mFavoriteUsersPresenter.unbindView()
            mFavoriteUsersPresenter = null
        }
        super.onDestroyView()
    }

    fun onFragmentResume() {
        super.onFragmentResume()
        mFavoriteUsersPresenter.getFavoriteUsers()
    }

    fun onGetAllUsers(userItemList: List<UserItem?>?) {
        Utils.runOnUiSafeThread {
            mTvNoData!!.visibility = View.GONE
            mAdapter.setListener(this)
            mAdapter.setListData(userItemList)
            mFavoriteUsersPresenter.getFavoriteUsers()
        }
    }

    override fun onNoFavoriteUsers() {
        mAdapter.setListFavoriteUsers(ArrayList<UserItem>())
        mTvNoData!!.visibility = View.GONE
    }

    override fun onNoUsers() {
        mTvNoData!!.visibility = View.VISIBLE
    }

    fun onGetFavoriteUsers(userItemList: List<UserItem?>?) {
        mAdapter.setListFavoriteUsers(userItemList)
    }

    override fun goToDetail(userItem: UserItem?) {
        ReputationActivity.start(this, userItem)
    }

    override fun saveAsFavorite(userItem: UserItem?) {
        mFavoriteUsersPresenter.saveFavoriteUser(userItem)
    }

    override fun onSaveFavoriteUsers() {
        mFavoriteUsersPresenter.getFavoriteUsers()
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}