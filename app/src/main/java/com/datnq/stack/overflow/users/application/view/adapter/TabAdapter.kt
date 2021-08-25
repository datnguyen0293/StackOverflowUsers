package com.datnq.stack.overflow.users.application.view.adapter

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.datnq.stack.overflow.users.GlobalApplication
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.Tab
import com.datnq.stack.overflow.users.application.view.adapter.viewholder.TabViewHolder
import com.datnq.stack.overflow.users.application.view.listener.FragmentListener
import com.datnq.stack.overflow.users.core.BaseRecyclerViewAdapter
import com.datnq.stack.overflow.users.databinding.TabItemBinding

class TabAdapter : BaseRecyclerViewAdapter<Tab, TabViewHolder>() {

    private var selectedPosition = 0
    private var listener: FragmentListener? = null

    fun setListener(pListener: FragmentListener?) {
        listener = pListener
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        return TabViewHolder(
            TabItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TabViewHolder, @SuppressLint("RecyclerView") position: Int) {
        getItemAt(position)?.let {
            holder.icon.setImageResource(it.icon)
            holder.text.setText(it.text)
        }

        holder.icon.setColorFilter(
            ContextCompat.getColor(GlobalApplication.getInstance(), R.color.grey),
            PorterDuff.Mode.SRC_IN
        )
        holder.text.setTextColor(ContextCompat.getColor(GlobalApplication.getInstance(), R.color.grey))

        if (selectedPosition == position) {
            holder.icon.setColorFilter(
                ContextCompat.getColor(GlobalApplication.getInstance(), R.color.red),
                PorterDuff.Mode.SRC_IN
            )
            holder.text.setTextColor(ContextCompat.getColor(GlobalApplication.getInstance(), R.color.red))
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            listener?.moveTo(position)
            notifyDataSetChanged()
        }
    }
}