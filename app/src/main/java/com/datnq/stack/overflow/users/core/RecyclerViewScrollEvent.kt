package com.datnq.stack.overflow.users.core

import androidx.recyclerview.widget.RecyclerView
import com.stone.vega.library.VegaLayoutManager

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
abstract class RecyclerViewScrollEvent protected constructor(private val mLayoutManager: VegaLayoutManager) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = mLayoutManager.childCount
        val currentTotalItemCount = mLayoutManager.itemCount
        val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()
        if (!isLoading()
            && !isLastPage(currentTotalItemCount.toLong())
            && firstVisibleItemPosition >= 0 && visibleItemCount + firstVisibleItemPosition >= currentTotalItemCount
        ) {
            loadMoreItems()
        }
    }

    protected abstract fun loadMoreItems()
    abstract fun isLastPage(currentTotalItemCount: Long): Boolean
    abstract fun isLoading(): Boolean

}