package com.datnq.stack.overflow.users.application.view.activity

import android.os.Bundle
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.view.fragment.AllUsersFragment
import com.datnq.stack.overflow.users.application.view.fragment.FavoriteUsersFragment
import com.datnq.stack.overflow.users.core.BaseActivity
import com.datnq.stack.overflow.users.core.BaseFragmentPagerAdapter
import com.datnq.stack.overflow.users.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val pagerAdapter = BaseFragmentPagerAdapter(this)
        pagerAdapter.addFragment(AllUsersFragment())
        pagerAdapter.addFragment(FavoriteUsersFragment())
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getString(if (position == 0) R.string.all else R.string.favorite)
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pagerAdapter.createFragment(tab.position).onFragmentResume()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })
    }

}