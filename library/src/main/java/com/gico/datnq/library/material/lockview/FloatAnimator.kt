package com.gico.datnq.library.material.lockview

import android.os.Handler
import android.os.Looper
import java.util.*
/**
 * Creates new instance.
 *
 * @param mStartValue    start value.
 * @param mEndValue      end value.
 * @param mDuration      duration, in milliseconds. This should not be long,
 * as delay value between animation frame is just 1 millisecond.
 */
class FloatAnimator(private val mStartValue: Float, private val mEndValue: Float, private val mDuration: Long) {
    /**
     * Gets animated value.
     *
     * @return animated value.
     */
    var animatedValue: Float
        private set
    private var mEventListeners: ArrayList<EventListener>? = null
    private var mHandler: Handler? = null
    private var mStartTime: Long = 0

    /**
     * Adds event listener.
     *
     * @param listener the listener.
     */
    fun addEventListener(listener: EventListener?) {
        if (listener == null) return
        mEventListeners?.add(listener)
    }

    /**
     * Starts animating.
     */
    fun start() {
        mHandler?.let { return }
        notifyAnimationStart()
        mStartTime = System.currentTimeMillis()
        mHandler = Handler(Looper.getMainLooper())
        mHandler?.post(object : Runnable {
            override fun run() {
                val handler = mHandler ?: return
                val elapsedTime = System.currentTimeMillis() - mStartTime
                if (elapsedTime > mDuration) {
                    mHandler = null
                    notifyAnimationEnd()
                } else {
                    val fraction = if (mDuration > 0) elapsedTime.toFloat() / mDuration else 1f
                    val delta = mEndValue - mStartValue
                    animatedValue = mStartValue + delta * fraction
                    notifyAnimationUpdate()
                    handler.postDelayed(this, ANIMATION_DELAY)
                }
            }
        })
    }

    /**
     * Cancels animating.
     */
    fun cancel() {
        if (mHandler == null) return
        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
        notifyAnimationCancel()
        notifyAnimationEnd()
    }

    /**
     * Notifies all listeners that animation starts.
     */
    private fun notifyAnimationStart() {
        val listeners: ArrayList<EventListener>? = mEventListeners
        listeners?.let {
            for (listener in it) listener.onAnimationStart(this)
        }
    }

    /**
     * Notifies all listeners that animation updates.
     */
    private fun notifyAnimationUpdate() {
        val listeners: ArrayList<EventListener>? = mEventListeners
        listeners?.let {
            for (listener in it) listener.onAnimationUpdate(this)
        }
    }

    /**
     * Notifies all listeners that animation cancels.
     */
    private fun notifyAnimationCancel() {
        val listeners: ArrayList<EventListener>? = mEventListeners
        listeners?.let {
            for (listener in it) listener.onAnimationCancel(this)
        }
    }

    /**
     * Notifies all listeners that animation ends.
     */
    private fun notifyAnimationEnd() {
        val listeners: ArrayList<EventListener>? = mEventListeners
        listeners?.let {
            for (listener in it) listener.onAnimationEnd(this)
        }
    }

    companion object {
        /**
         * Animation delay, in milliseconds.
         */
        private const val ANIMATION_DELAY: Long = 1
    }

    init {
        mEventListeners = ArrayList()
        animatedValue = mStartValue
    }
}