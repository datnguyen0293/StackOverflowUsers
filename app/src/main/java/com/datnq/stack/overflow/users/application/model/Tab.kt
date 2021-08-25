package com.datnq.stack.overflow.users.application.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class Tab(@DrawableRes ic: Int, @StringRes txt: Int) {
    var icon: Int = ic
    var text: Int = txt
}