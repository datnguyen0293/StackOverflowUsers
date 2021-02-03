package com.datnq.stack.overflow.users.application.view.activity

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.ToxicBakery.viewpager.transforms.AccordionTransformer
import java.util.*

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class MainActivity : BaseActivity(R.layout.activity_main) {
    @BindView(R.id.tab_layout)
    var mTabLayout: TabLayout? = null

    @BindView(R.id.view_pager)
    var mViewPager: ViewPager? = null

    @Inject
    var mAllUsersFragment: AllUsersFragment? = null

    @Inject
    var mFavoriteUsersFragment: FavoriteUsersFragment? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewPager()
        setSelectedTab(0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViewPager() {
        val mPagerAdapter = UsersPagerAdapter(fragmentManager())
        mPagerAdapter.addFragment(mAllUsersFragment, getString(R.string.all))
        mPagerAdapter.addFragment(mFavoriteUsersFragment, getString(R.string.favorite))
        mViewPager.setAdapter(mPagerAdapter)
        mViewPager.setPageTransformer(true, AccordionTransformer())
        mTabLayout.setupWithViewPager(mViewPager, true)
        val homeTabView: View = getLayoutInflater().inflate(R.layout.tab_item, mTabLayout, false)
        (homeTabView.findViewById<View>(R.id.icon) as AppCompatImageView).setImageResource(R.drawable.ic_home)
        (homeTabView.findViewById<View>(R.id.text) as AppCompatTextView).setText(getString(R.string.all))
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setCustomView(homeTabView)
        val favoriteTabView: View =
            getLayoutInflater().inflate(R.layout.tab_item, mTabLayout, false)
        (favoriteTabView.findViewById<View>(R.id.icon) as AppCompatImageView).setImageResource(R.drawable.ic_favorite)
        (favoriteTabView.findViewById<View>(R.id.text) as AppCompatTextView).setText(getString(R.string.favorite))
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setCustomView(favoriteTabView)
        mTabLayout.addOnTabSelectedListener(object :
            TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                super.onTabSelected(tab)
                setSelectedTab(tab.getPosition())
                mPagerAdapter.getItem(tab.getPosition()).onFragmentResume()
            }
        })
    }

    private fun setSelectedTab(position: Int) {
        val size: Int = mTabLayout.getTabCount()
        for (i in 0 until size) {
            val tab: TabLayout.Tab = mTabLayout.getTabAt(i)
            if (tab != null) {
                val view: View = tab.getCustomView()
                if (view != null) {
                    (view.findViewById<View>(R.id.icon) as AppCompatImageView).setColorFilter(
                        ContextCompat.getColor(
                            activity(),
                            if (i == position) R.color.red else R.color.grey
                        ),
                        PorterDuff.Mode.SRC_IN
                    )
                    (view.findViewById<View>(R.id.text) as AppCompatTextView).setTextColor(
                        ContextCompat.getColor(
                            activity(),
                            if (i == position) R.color.red else R.color.grey
                        )
                    )
                }
            }
        }
    }
}