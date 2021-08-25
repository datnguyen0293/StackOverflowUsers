package com.datnq.stack.overflow.users.application.view.adapter.viewholder

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.datnq.stack.overflow.users.databinding.TabItemBinding

class TabViewHolder(binding: TabItemBinding): RecyclerView.ViewHolder(binding.root) {
    val icon: AppCompatImageView = binding.imvIcon
    val text: AppCompatTextView = binding.text
}