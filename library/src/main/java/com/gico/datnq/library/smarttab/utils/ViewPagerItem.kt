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
import androidx.annotation.LayoutRes

class ViewPagerItem constructor(title: CharSequence?, width: Float, @param:LayoutRes private val resource: Int) : PagerItem(title, width) {
    fun initiate(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(resource, container, false)
    }

    companion object {
        @JvmStatic
        fun of(title: CharSequence?, @LayoutRes resource: Int): ViewPagerItem {
            return of(title, PagerItem.DEFAULT_WIDTH, resource)
        }

        @JvmStatic
        fun of(title: CharSequence?, width: Float, @LayoutRes resource: Int): ViewPagerItem {
            return ViewPagerItem(title, width, resource)
        }
    }
}