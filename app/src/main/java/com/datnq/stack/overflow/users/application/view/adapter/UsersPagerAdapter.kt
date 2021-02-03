package com.datnq.stack.overflow.users.application.view.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.datnq.stack.overflow.users.core.BaseFragment
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class UsersPagerAdapter(fragmentManager: FragmentManager?) : FragmentPagerAdapter(
    fragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val mLstFragments: MutableList<BaseFragment>?
    private val mLstFragmentsTitles: MutableList<String>
    fun addFragment(fragment: BaseFragment, title: String) {
        mLstFragments!!.add(fragment)
        mLstFragmentsTitles.add(title)
    }

    override fun getCount(): Int {
        return mLstFragments?.size ?: 0
    }

    override fun getItem(position: Int): BaseFragment {
        return mLstFragments!![position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mLstFragmentsTitles[position]
    }

    init {
        mLstFragments = ArrayList<BaseFragment>()
        mLstFragmentsTitles = ArrayList()
    }
}