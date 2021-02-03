package com.datnq.stack.overflow.users.datnq.library.material.lockview

import android.animation.ValueAnimator

internal class CellState {

    companion object {
        const val SCALE = 1.0f
        const val TRANSLATE_Y = 0.0f
        const val ALPHA = 1.0f
    }

    @JvmField
    var size: Float = 0f
    @JvmField
    var lineEndX = Float.MIN_VALUE
    @JvmField
    var lineEndY = Float.MIN_VALUE
    @JvmField
    var lineAnimator: ValueAnimator? = null

}