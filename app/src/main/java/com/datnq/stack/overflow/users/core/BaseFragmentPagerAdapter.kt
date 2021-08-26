package com.datnq.stack.overflow.users.core

import androidx.viewpager2.adapter.FragmentStateAdapter

class BaseFragmentPagerAdapter(activity: BaseActivity<*>): FragmentStateAdapter(activity) {

    private var listFragments: ArrayList<BaseFragment<*>> = ArrayList()

    fun addFragment(fragment: BaseFragment<*>) {
        listFragments.add(fragment)
    }

    override fun getItemCount(): Int {
        return listFragments.size
    }

    override fun createFragment(position: Int): BaseFragment<*> {
        return listFragments[position]
    }

}