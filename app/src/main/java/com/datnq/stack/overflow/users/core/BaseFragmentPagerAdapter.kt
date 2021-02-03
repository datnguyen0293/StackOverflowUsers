package com.datnq.stack.overflow.users.core

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class BaseFragmentPagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var listFragments: ArrayList<BaseFragment> = ArrayList()
    private var listTitles: ArrayList<String> = ArrayList()

    fun addFragment(fragment: BaseFragment, title: String) {
        listFragments.add(fragment)
        listTitles.add(title)
    }

    override fun getCount(): Int {
        return listFragments.size
    }

    override fun getItem(position: Int): BaseFragment {
        return listFragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return listTitles[position]
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }
}