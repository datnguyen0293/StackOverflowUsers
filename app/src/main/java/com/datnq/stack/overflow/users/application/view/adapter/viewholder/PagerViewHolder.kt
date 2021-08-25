package com.datnq.stack.overflow.users.application.view.adapter.viewholder

import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.datnq.stack.overflow.users.databinding.PagerItemBinding

class PagerViewHolder(binding: PagerItemBinding):RecyclerView.ViewHolder(binding.root) {
    val flContainer: FrameLayout = binding.flContainer
}