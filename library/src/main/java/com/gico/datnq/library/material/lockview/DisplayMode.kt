package com.datnq.stack.overflow.users.datnq.library.material.lockview

/**
 * How to display the current pattern.
 */
enum class DisplayMode {
    /**
     * The pattern drawn is correct (i.e draw it in a friendly color)
     */
    CORRECT,

    /**
     * Animate the pattern (for demo, and help).
     */
    ANIMATE,

    /**
     * The pattern is wrong (i.e draw a foreboding color)
     */
    WRONG
}