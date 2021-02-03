package com.datnq.stack.overflow.users.application.view.fragment

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.datnq.stack.overflow.users.R

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class FavoriteUsersFragment : BaseFragment(R.layout.fragment_favorite_users), GetFavoriteUsersView,
    UsersListener {
    @BindView(R.id.rcvUsers)
    var mRcvUsers: RecyclerView? = null

    @BindView(R.id.tvNoData)
    var mTvNoData: AppCompatTextView? = null

    @Inject
    var mAdapter: FavoriteUsersAdapter? = null

    @Inject
    var mPresenter: FavoriteUsersPresenter? = null
    private fun initializeRecyclerView() {
        mAdapter.setListener(this)
        mRcvUsers.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.activity())
        mRcvUsers.setLayoutManager(linearLayoutManager)
        mRcvUsers.setAdapter(mAdapter)
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
        mPresenter.bindView(this)
    }

    fun onDestroyView() {
        if (mPresenter != null) {
            mPresenter.unbindView()
            mPresenter = null
        }
        super.onDestroyView()
    }

    fun onFragmentResume() {
        super.onFragmentResume()
        mPresenter.getFavoriteUsers()
    }

    override fun goToDetail(userItem: UserItem?) {
        // Don't go to detail
    }

    override fun saveAsFavorite(userItem: UserItem?) {
        mPresenter.saveFavoriteUser(userItem)
    }

    fun onGetFavoriteUsers(userItemList: List<UserItem?>?) {
        Utils.runOnUiSafeThread {
            mTvNoData!!.visibility = View.GONE
            mAdapter.setListData(userItemList)
        }
    }

    override fun onNoFavoriteUsers() {
        mTvNoData!!.visibility = View.VISIBLE
    }

    override fun onSaveFavoriteUsers() {
        mPresenter.getFavoriteUsers()
    }
}