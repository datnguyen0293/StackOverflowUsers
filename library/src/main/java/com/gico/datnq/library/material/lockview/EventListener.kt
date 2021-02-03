package com.datnq.stack.overflow.users.datnq.library.material.lockview

/**
 * Event listener.
 *
 * @author Hai Bison
 */
interface EventListener {
    /**
     * Will be called when animation starts.
     *
     * @param animator the animator.
     */
    fun onAnimationStart(animator: FloatAnimator)

    /**
     * Will be called when new animated value is calculated.
     *
     * @param animator the animator.
     */
    fun onAnimationUpdate(animator: FloatAnimator)

    /**
     * Will be called when animation cancels.
     *
     * @param animator the animator.
     */
    fun onAnimationCancel(animator: FloatAnimator)

    /**
     * Will be called when animation ends.
     *
     * @param animator the animator.
     */
    fun onAnimationEnd(animator: FloatAnimator)
}