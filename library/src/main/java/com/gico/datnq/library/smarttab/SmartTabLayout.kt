/**
 * Copyright (C) 2015 ogaclejapan
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gico.datnq.library.smarttab

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.gico.datnq.library.R
import com.gico.datnq.library.smarttab.SmartTabLayout.TabColorizer
import kotlin.math.roundToInt

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as
 * to
 * the user's scroll progress.
 *
 *
 * To use the component, simply add it to your view hierarchy. Then in your
 * is being used for.
 *
 *
 * The colors can be customized in two ways. The first and simplest is to provide an array of
 * colors
 * via [.setSelectedIndicatorColors] and [.setDividerColors]. The
 * alternative is via the [TabColorizer] interface which provides you complete control over
 * which color is used for any individual position.
 *
 *
 * The views used as tabs can be customized by calling [.setCustomTabView],
 * providing the layout ID of your custom layout.
 *
 *
 * Forked from Google Samples &gt; SlidingTabsBasic &gt;
 * [SlidingTabLayout](https://developer.android.com/samples/SlidingTabsBasic/src/com.example.android.common/view/SlidingTabLayout.html)
 */
class SmartTabLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : HorizontalScrollView(context, attrs, defStyle) {
    private val tabStrip: SmartTabStrip
    private val titleOffset: Int
    private val tabViewBackgroundResId: Int
    private val tabViewTextAllCaps: Boolean
    private lateinit var tabViewTextColors: ColorStateList
    private val tabViewTextSize: Float
    private val tabViewTextHorizontalPadding: Int
    private val tabViewTextMinWidth: Int
    private var viewPager: ViewPager? = null
    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener? = null
    private var onScrollChangeListener: OnScrollChangeListener? = null
    private var tabProvider: TabProvider? = null
    private val internalTabClickListener: InternalTabClickListener?
    private var onTabClickListener: OnTabClickListener? = null
    private var distributeEvenly: Boolean
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangeListener?.onScrollChanged(l, oldl)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (tabStrip.isIndicatorAlwaysInCenter && tabStrip.childCount > 0) {
            val firstTab = tabStrip.getChildAt(0)
            val lastTab = tabStrip.getChildAt(tabStrip.childCount - 1)
            val start = (w - Utils.getMeasuredWidth(firstTab)) / 2 - Utils.getMarginStart(firstTab)
            val end = (w - Utils.getMeasuredWidth(lastTab)) / 2 - Utils.getMarginEnd(lastTab)
            tabStrip.minimumWidth = tabStrip.measuredWidth
            ViewCompat.setPaddingRelative(this, start, paddingTop, end, paddingBottom)
            clipToPadding = false
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // Ensure first scroll
        if (changed) {
            viewPager?.currentItem?.let { scrollToTab(it, 0f) }
        }
    }

    /**
     * Set the behavior of the Indicator scrolling feedback.
     *
     * @param interpolator [com.ogaclejapan.smarttablayout.SmartTabIndicationInterpolator]
     */
    fun setIndicationInterpolator(interpolator: SmartTabIndicationInterpolator) {
        tabStrip.setIndicationInterpolator(interpolator)
    }

    /**
     * Set the custom [TabColorizer] to be used.
     *
     * If you only require simple customisation then you can use
     * [.setSelectedIndicatorColors] and [.setDividerColors] to achieve
     * similar effects.
     */
    fun setCustomTabColorizer(tabColorizer: TabColorizer?) {
        tabStrip.setCustomTabColorizer(tabColorizer)
    }

    /**
     * Set the color used for styling the tab text. This will need to be called prior to calling
     *
     * @param color to use for tab text
     */
    fun setDefaultTabTextColor(color: Int) {
        tabViewTextColors = ColorStateList.valueOf(color)
    }

    /**
     * Sets the colors used for styling the tab text. This will need to be called prior to calling
     *
     * @param colors ColorStateList to use for tab text
     */
    fun setDefaultTabTextColor(colors: ColorStateList) {
        tabViewTextColors = colors
    }

    /**
     * Set the same weight for tab
     */
    fun setDistributeEvenly(distributeEvenly: Boolean) {
        this.distributeEvenly = distributeEvenly
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    fun setSelectedIndicatorColors(vararg colors: Int) {
        tabStrip.setSelectedIndicatorColors(*colors)
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    fun setDividerColors(vararg colors: Int) {
        tabStrip.setDividerColors(*colors)
    }

    /**
     * Set the [ViewPager.OnPageChangeListener]. When using [SmartTabLayout] you are
     * required to set any [ViewPager.OnPageChangeListener] through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager.setOnPageChangeListener
     */
    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener?) {
        viewPagerPageChangeListener = listener
    }

    /**
     * Set [OnScrollChangeListener] for obtaining values of scrolling.
     *
     * @param listener the [OnScrollChangeListener] to set
     */
    fun setOnScrollChangeListener(listener: OnScrollChangeListener?) {
        onScrollChangeListener = listener
    }

    /**
     * Set [OnTabClickListener] for obtaining click event.
     *
     * @param listener the [OnTabClickListener] to set
     */
    fun setOnTabClickListener(listener: OnTabClickListener?) {
        onTabClickListener = listener
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId id of the [android.widget.TextView] in the inflated view
     */
    private fun setCustomTabView(layoutResId: Int, textViewId: Int) {
        tabProvider = SimpleTabProvider(context, layoutResId, textViewId)
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param provider [TabProvider]
     */
    fun setCustomTabView(provider: TabProvider?) {
        tabProvider = provider
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    fun setViewPager(viewPager: ViewPager?) {
        tabStrip.removeAllViews()
        this.viewPager = viewPager
        this.viewPager?.addOnPageChangeListener(InternalViewPagerListener())
        populateTabStrip()
    }

    /**
     * Returns the view at the specified position in the tabs.
     *
     * @param position the position at which to get the view from
     * @return the view at the specified position or null if the position does not exist within the
     * tabs
     */
    fun getTabAt(position: Int): View {
        return tabStrip.getChildAt(position)
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * [.setCustomTabView].
     */
    private fun createDefaultTabView(title: CharSequence?): TextView {
        val textView = TextView(context)
        textView.gravity = Gravity.CENTER
        textView.text = title
        textView.setTextColor(tabViewTextColors)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabViewTextSize)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        if (tabViewBackgroundResId != NO_ID) {
            textView.setBackgroundResource(tabViewBackgroundResId)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true)
            textView.setBackgroundResource(outValue.resourceId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.isAllCaps = tabViewTextAllCaps
        }
        textView.setPadding(
                tabViewTextHorizontalPadding, 0,
                tabViewTextHorizontalPadding, 0)
        if (tabViewTextMinWidth > 0) {
            textView.minWidth = tabViewTextMinWidth
        }
        return textView
    }

    private fun populateTabStrip() {
        val adapter = viewPager?.adapter
        adapter?.count?.let {
            for (i in 0 until it) {
                val tabView = (if (tabProvider == null) createDefaultTabView(adapter.getPageTitle(i)) else tabProvider?.createTabView(tabStrip, i, adapter))
                        ?: throw IllegalStateException("tabView is null.")
                if (distributeEvenly) {
                    val lp = tabView.layoutParams as LinearLayout.LayoutParams
                    lp.width = 0
                    lp.weight = 1f
                }
                internalTabClickListener?.let { l ->
                    tabView.setOnClickListener(l)
                }
                tabStrip.addView(tabView)
                if (i == viewPager?.currentItem) {
                    tabView.isSelected = true
                }
            }
        }
    }

    private fun scrollToTab(tabIndex: Int, positionOffset: Float) {
        val tabStripChildCount = tabStrip.childCount
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return
        }
        val isLayoutRtl = Utils.isLayoutRtl(this)
        val selectedTab = tabStrip.getChildAt(tabIndex)
        val widthPlusMargin = Utils.getWidth(selectedTab) + Utils.getMarginHorizontally(selectedTab)
        var extraOffset = (positionOffset * widthPlusMargin).toInt()
        if (tabStrip.isIndicatorAlwaysInCenter) {
            if (0f < positionOffset && positionOffset < 1f) {
                val nextTab = tabStrip.getChildAt(tabIndex + 1)
                val selectHalfWidth = Utils.getWidth(selectedTab) / 2 + Utils.getMarginEnd(selectedTab)
                val nextHalfWidth = Utils.getWidth(nextTab) / 2 + Utils.getMarginStart(nextTab)
                extraOffset = (positionOffset * (selectHalfWidth + nextHalfWidth)).roundToInt()
            }
            val firstTab = tabStrip.getChildAt(0)
            var x: Int
            if (isLayoutRtl) {
                val first = Utils.getWidth(firstTab) + Utils.getMarginEnd(firstTab)
                val selected = Utils.getWidth(selectedTab) + Utils.getMarginEnd(selectedTab)
                x = Utils.getEnd(selectedTab) - Utils.getMarginEnd(selectedTab) - extraOffset
                x -= (first - selected) / 2
            } else {
                val first = Utils.getWidth(firstTab) + Utils.getMarginStart(firstTab)
                val selected = Utils.getWidth(selectedTab) + Utils.getMarginStart(selectedTab)
                x = Utils.getStart(selectedTab) - Utils.getMarginStart(selectedTab) + extraOffset
                x -= (first - selected) / 2
            }
            scrollTo(x, 0)
            return
        }
        var x: Int
        if (titleOffset == TITLE_OFFSET_AUTO_CENTER) {
            if (0f < positionOffset && positionOffset < 1f) {
                val nextTab = tabStrip.getChildAt(tabIndex + 1)
                val selectHalfWidth = Utils.getWidth(selectedTab) / 2 + Utils.getMarginEnd(selectedTab)
                val nextHalfWidth = Utils.getWidth(nextTab) / 2 + Utils.getMarginStart(nextTab)
                extraOffset = (positionOffset * (selectHalfWidth + nextHalfWidth)).roundToInt()
            }
            if (isLayoutRtl) {
                x = -Utils.getWidthWithMargin(selectedTab) / 2 + width / 2
                x -= Utils.getPaddingStart(this)
            } else {
                x = Utils.getWidthWithMargin(selectedTab) / 2 - width / 2
                x += Utils.getPaddingStart(this)
            }
        } else {
            x = if (isLayoutRtl) {
                if (tabIndex > 0 || positionOffset > 0) titleOffset else 0
            } else {
                if (tabIndex > 0 || positionOffset > 0) -titleOffset else 0
            }
        }
        val start = Utils.getStart(selectedTab)
        val startMargin = Utils.getMarginStart(selectedTab)
        x += if (isLayoutRtl) {
            start + startMargin - extraOffset - width + Utils.getPaddingHorizontally(this)
        } else {
            start - startMargin + extraOffset
        }
        scrollTo(x, 0)
    }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * [.setCustomTabColorizer].
     */
    interface TabColorizer {
        /**
         * @return return the color of the indicator used when `position` is selected.
         */
        fun getIndicatorColor(position: Int): Int

        /**
         * @return return the color of the divider drawn to the right of `position`.
         */
        fun getDividerColor(position: Int): Int
    }

    /**
     * Interface definition for a callback to be invoked when the scroll position of a view changes.
     */
    interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param scrollX Current horizontal scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         */
        fun onScrollChanged(scrollX: Int, oldScrollX: Int)
    }

    /**
     * Interface definition for a callback to be invoked when a tab is clicked.
     */
    interface OnTabClickListener {
        /**
         * Called when a tab is clicked.
         *
         * @param position tab's position
         */
        fun onTabClicked(position: Int)
    }

    /**
     * Create the custom tabs in the tab layout. Set with
     * [.setCustomTabView]
     */
    interface TabProvider {
        /**
         * @return Return the View of `position` for the Tabs
         */
        fun createTabView(container: ViewGroup?, position: Int, adapter: PagerAdapter?): View?
    }

    private class SimpleTabProvider(context: Context, layoutResId: Int, textViewId: Int) : TabProvider {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val tabViewLayoutId: Int = layoutResId
        private val tabViewTextViewId: Int = textViewId
        override fun createTabView(container: ViewGroup?, position: Int, adapter: PagerAdapter?): View? {
            var tabView: View? = null
            var tabTitleView: TextView? = null
            if (tabViewLayoutId != NO_ID) {
                tabView = inflater.inflate(tabViewLayoutId, container, false)
            }
            if (tabViewTextViewId != NO_ID) {
                tabTitleView = tabView?.findViewById<View>(tabViewTextViewId) as TextView
            }
            if (tabTitleView == null && TextView::class.java.isInstance(tabView)) {
                tabTitleView = tabView as TextView?
            }
            tabTitleView?.text = adapter?.getPageTitle(position)
            return tabView
        }

    }

    private inner class InternalViewPagerListener : ViewPager.OnPageChangeListener {
        private var scrollState = 0
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val tabStripChildCount = tabStrip.childCount
            if (tabStripChildCount == 0 || position < 0 || position >= tabStripChildCount) {
                return
            }
            tabStrip.onViewPagerPageChanged(position, positionOffset)
            scrollToTab(position, positionOffset)
            viewPagerPageChangeListener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            scrollState = state
            viewPagerPageChangeListener?.onPageScrollStateChanged(state)
        }

        override fun onPageSelected(position: Int) {
            if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                tabStrip.onViewPagerPageChanged(position, 0f)
                scrollToTab(position, 0f)
            }
            var i = 0
            val size = tabStrip.childCount
            while (i < size) {
                tabStrip.getChildAt(i).isSelected = position == i
                i++
            }
            viewPagerPageChangeListener?.onPageSelected(position)
        }
    }

    private inner class InternalTabClickListener : OnClickListener {
        override fun onClick(v: View) {
            for (i in 0 until tabStrip.childCount) {
                if (v === tabStrip.getChildAt(i)) {
                    onTabClickListener?.onTabClicked(i)
                    viewPager?.currentItem = i
                    return
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_DISTRIBUTE_EVENLY = false
        private const val TITLE_OFFSET_DIPS = 24
        private const val TITLE_OFFSET_AUTO_CENTER = -1
        private const val TAB_VIEW_PADDING_DIPS = 16
        private const val TAB_VIEW_TEXT_ALL_CAPS = true
        private const val TAB_VIEW_TEXT_SIZE_SP = 12
        private const val TAB_VIEW_TEXT_COLOR = -0x4000000
        private const val TAB_VIEW_TEXT_MIN_WIDTH = 0
        private const val TAB_CLICKABLE = true
    }

    init {

        // Disable the Scroll Bar
        isHorizontalScrollBarEnabled = false
        val dm = resources.displayMetrics
        val density = dm.density
        var tabBackgroundResId = NO_ID
        var textAllCaps = TAB_VIEW_TEXT_ALL_CAPS
        var textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP.toFloat(), dm)
        var textHorizontalPadding = (TAB_VIEW_PADDING_DIPS * density).toInt()
        var textMinWidth = (TAB_VIEW_TEXT_MIN_WIDTH * density).toInt()
        var distributeEvenly = DEFAULT_DISTRIBUTE_EVENLY
        var customTabLayoutId = NO_ID
        var customTabTextViewId = NO_ID
        var clickable = TAB_CLICKABLE
        var titleOffset = (TITLE_OFFSET_DIPS * density).toInt()
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.stl_SmartTabLayout, defStyle, 0)
        tabBackgroundResId = a.getResourceId(
                R.styleable.stl_SmartTabLayout_stl_defaultTabBackground, tabBackgroundResId)
        textAllCaps = a.getBoolean(
                R.styleable.stl_SmartTabLayout_stl_defaultTabTextAllCaps, textAllCaps)
        a.getColorStateList(R.styleable.stl_SmartTabLayout_stl_defaultTabTextColor)?.let { tabViewTextColors = it }
        textSize = a.getDimension(
                R.styleable.stl_SmartTabLayout_stl_defaultTabTextSize, textSize)
        textHorizontalPadding = a.getDimensionPixelSize(
                R.styleable.stl_SmartTabLayout_stl_defaultTabTextHorizontalPadding, textHorizontalPadding)
        textMinWidth = a.getDimensionPixelSize(
                R.styleable.stl_SmartTabLayout_stl_defaultTabTextMinWidth, textMinWidth)
        customTabLayoutId = a.getResourceId(
                R.styleable.stl_SmartTabLayout_stl_customTabTextLayoutId, customTabLayoutId)
        customTabTextViewId = a.getResourceId(
                R.styleable.stl_SmartTabLayout_stl_customTabTextViewId, customTabTextViewId)
        distributeEvenly = a.getBoolean(
                R.styleable.stl_SmartTabLayout_stl_distributeEvenly, distributeEvenly)
        clickable = a.getBoolean(
                R.styleable.stl_SmartTabLayout_stl_clickable, clickable)
        titleOffset = a.getLayoutDimension(
                R.styleable.stl_SmartTabLayout_stl_titleOffset, titleOffset)
        a.recycle()
        this.titleOffset = titleOffset
        tabViewBackgroundResId = tabBackgroundResId
        tabViewTextAllCaps = textAllCaps
        tabViewTextSize = textSize
        tabViewTextHorizontalPadding = textHorizontalPadding
        tabViewTextMinWidth = textMinWidth
        internalTabClickListener = if (clickable) InternalTabClickListener() else null
        this.distributeEvenly = distributeEvenly
        if (customTabLayoutId != NO_ID) {
            setCustomTabView(customTabLayoutId, customTabTextViewId)
        }
        tabStrip = SmartTabStrip(context, attrs)
        if (distributeEvenly && tabStrip.isIndicatorAlwaysInCenter) {
            throw UnsupportedOperationException(
                    "'distributeEvenly' and 'indicatorAlwaysInCenter' both use does not support")
        }

        // Make sure that the Tab Strips fills this View
        isFillViewport = !tabStrip.isIndicatorAlwaysInCenter
        addView(tabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
}