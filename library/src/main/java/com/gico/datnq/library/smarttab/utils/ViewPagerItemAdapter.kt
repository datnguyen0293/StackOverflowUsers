/**
 * Copyright (C) 2015 ogaclejapan
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
package com.gico.datnq.library.smarttab.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.viewpager.widget.PagerAdapter
import java.lang.ref.WeakReference

class ViewPagerItemAdapter(private val pages: ViewPagerItems) : PagerAdapter() {
    private val holder: SparseArrayCompat<WeakReference<View?>> = SparseArrayCompat(pages.size)
    private val inflater: LayoutInflater = LayoutInflater.from(pages.context)

    override fun getCount(): Int {
        return pages.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getPagerItem(position)?.initiate(inflater, container)
        container.addView(view)
        holder.put(position, WeakReference(view))
        return view ?: container
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        holder.remove(position)
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return obj === view
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return getPagerItem(position)?.title
    }

    override fun getPageWidth(position: Int): Float {
        return getPagerItem(position)?.width ?: 0F
    }

    fun getPage(position: Int): View? {
        val weakRefItem = holder[position]
        return weakRefItem?.get()
    }

    private fun getPagerItem(position: Int): ViewPagerItem? {
        return pages[position]
    }

}