package com.datnq.stack.overflow.users.application.view.activity

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.ToxicBakery.viewpager.transforms.AccordionTransformer
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.view.adapter.UsersPagerAdapter
import com.datnq.stack.overflow.users.application.view.fragment.AllUsersFragment
import com.datnq.stack.overflow.users.application.view.fragment.FavoriteUsersFragment
import com.datnq.stack.overflow.users.core.BaseActivity
import com.datnq.stack.overflow.users.databinding.ActivityMainBinding
import com.datnq.stack.overflow.users.databinding.TabItemBinding
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class MainActivity : BaseActivity() {

    @Inject
    lateinit var mAllUsersFragment: AllUsersFragment
    @Inject
    lateinit var mFavoriteUsersFragment: FavoriteUsersFragment
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewPager()
        setSelectedTab(0)
    }

    private fun setupViewPager() {
        val mPagerAdapter = UsersPagerAdapter(supportFragmentManager)
        mPagerAdapter.addFragment(mAllUsersFragment, getString(R.string.all))
        mPagerAdapter.addFragment(mFavoriteUsersFragment, getString(R.string.favorite))
        binding.viewPager.adapter = mPagerAdapter
        binding.viewPager.setPageTransformer(true, AccordionTransformer())
        binding.tabLayout.setupWithViewPager(binding.viewPager, true)
        val homeTabView = TabItemBinding.inflate(layoutInflater, binding.tabLayout, false)
        homeTabView.imvIcon.setImageResource(R.drawable.ic_home)
        homeTabView.text.text = getString(R.string.all)
        binding.tabLayout.getTabAt(0)?.customView = homeTabView.root
        val favoriteTabView = TabItemBinding.inflate(layoutInflater, binding.tabLayout, false)
        favoriteTabView.imvIcon.setImageResource(R.drawable.ic_favorite)
        favoriteTabView.text.text = getString(R.string.favorite)
        binding.tabLayout.getTabAt(1)?.customView = favoriteTabView.root
        binding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                super.onTabSelected(tab)
                setSelectedTab(tab.position)
                mPagerAdapter.getItem(tab.position).onFragmentResume()
            }
        })
    }

    private fun setSelectedTab(position: Int) {
        when (position) {
            0 -> {
                val homeTabView =
                    TabItemBinding.inflate(layoutInflater, binding.tabLayout, false)
                homeTabView.imvIcon.setImageResource(R.drawable.ic_home)
                homeTabView.imvIcon.setColorFilter(
                    ContextCompat.getColor(this, R.color.red),
                    PorterDuff.Mode.SRC_IN
                )
                homeTabView.text.setTextColor(ContextCompat.getColor(this, R.color.red))
                homeTabView.text.text = getString(R.string.all)
                binding.tabLayout.getTabAt(0)?.customView = homeTabView.root
                val favoriteTabView =
                    TabItemBinding.inflate(layoutInflater, binding.tabLayout, false)
                favoriteTabView.imvIcon.setImageResource(R.drawable.ic_favorite)
                favoriteTabView.imvIcon.setColorFilter(
                    ContextCompat.getColor(this, R.color.grey),
                    PorterDuff.Mode.SRC_IN
                )
                favoriteTabView.text.text = getString(R.string.favorite)
                favoriteTabView.text.setTextColor(ContextCompat.getColor(this, R.color.grey))
                binding.tabLayout.getTabAt(1)?.customView = favoriteTabView.root
            }
            1 -> {
                val homeTabView =
                    TabItemBinding.inflate(layoutInflater, binding.tabLayout, false)
                homeTabView.imvIcon.setImageResource(R.drawable.ic_home)
                homeTabView.imvIcon.setColorFilter(
                    ContextCompat.getColor(this, R.color.grey),
                    PorterDuff.Mode.SRC_IN
                )
                homeTabView.text.text = getString(R.string.all)
                homeTabView.text.setTextColor(ContextCompat.getColor(this, R.color.grey))
                binding.tabLayout.getTabAt(0)?.customView = homeTabView.root
                val favoriteTabView =
                    TabItemBinding.inflate(layoutInflater, binding.tabLayout, false)
                favoriteTabView.imvIcon.setImageResource(R.drawable.ic_favorite)
                favoriteTabView.imvIcon.setColorFilter(
                    ContextCompat.getColor(this, R.color.red),
                    PorterDuff.Mode.SRC_IN
                )
                favoriteTabView.text.text = getString(R.string.favorite)
                favoriteTabView.text.setTextColor(ContextCompat.getColor(this, R.color.red))
                binding.tabLayout.getTabAt(1)?.customView = favoriteTabView.root
            }
        }
        binding.root.requestLayout()
    }

}