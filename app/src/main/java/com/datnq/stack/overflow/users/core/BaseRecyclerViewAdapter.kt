package com.datnq.stack.overflow.users.core

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<M, V: RecyclerView.ViewHolder>: RecyclerView.Adapter<V>() {

    private var data: ArrayList<M>? = null
    
    fun getData(): ArrayList<M>? {
        return data
    }

    fun setData(d: ArrayList<M>?) {
        if (data == null) {
            data = ArrayList()
        } else {
            data?.clear()
        }
        d?.let { data?.addAll(it) }
        notifyDataSetChanged()
    }

    fun addData(d: ArrayList<M>) {
        if (data == null) {
            data = ArrayList()
        }
        data?.addAll(d)
        notifyDataSetChanged()
    }

    protected fun getItemAt(position: Int): M? {
        if (-1 < position && position < itemCount) {
            return data?.get(position)
        }
        return null
    }

    protected abstract fun getLayoutResourceId(): Int

    protected fun getView(@NonNull parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(getLayoutResourceId(), parent, false)
    }

    override fun getItemCount(): Int {
        data?.size?.let { return it }
        return 0
    }
}