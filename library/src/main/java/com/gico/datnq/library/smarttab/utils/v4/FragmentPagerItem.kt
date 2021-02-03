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
package com.datnq.stack.overflow.users.datnq.library.smarttab.utils.v4

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.datnq.stack.overflow.users.datnq.library.smarttab.utils.PagerItem

@Suppress("DEPRECATION")
class FragmentPagerItem constructor(title: CharSequence?, width: Float, private val className: String, private val args: Bundle) : PagerItem(title, width) {
    fun instantiate(context: Context?, position: Int): Fragment {
        setPosition(args, position)
        return context ?.let { Fragment.instantiate(it, className, args) } ?: run { Fragment() }
    }

    companion object {
        private const val TAG = "FragmentPagerItem"
        private const val KEY_POSITION = "$TAG:Position"
        fun of(title: CharSequence?, clazz: Class<out Fragment?>): FragmentPagerItem {
            return of(title, PagerItem.DEFAULT_WIDTH, clazz)
        }

        fun of(title: CharSequence?, clazz: Class<out Fragment?>,
               args: Bundle): FragmentPagerItem {
            return of(title, PagerItem.DEFAULT_WIDTH, clazz, args)
        }

        @JvmOverloads
        fun of(title: CharSequence?, width: Float,
               clazz: Class<out Fragment?>, args: Bundle = Bundle()): FragmentPagerItem {
            return FragmentPagerItem(title, width, clazz.name, args)
        }

        private fun hasPosition(args: Bundle?): Boolean {
            return args?.containsKey(KEY_POSITION) ?: run { false }
        }

        fun getPosition(args: Bundle): Int {
            return if (hasPosition(args)) args.getInt(KEY_POSITION) else 0
        }

        fun setPosition(args: Bundle, position: Int) {
            args.putInt(KEY_POSITION, position)
        }
    }
}