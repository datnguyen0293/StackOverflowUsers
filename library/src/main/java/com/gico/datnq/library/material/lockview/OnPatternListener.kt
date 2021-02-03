package com.gico.datnq.library.material.lockview

/**
 * The call back abstract class for detecting patterns entered by the user.
 */
interface OnPatternListener {
    /**
     * A new pattern has begun.
     */
    fun onPatternStart()

    /**
     * The pattern was cleared.
     */
    fun onPatternCleared()

    /**
     * The user extended the pattern currently being drawn by one cell.
     *
     * @param pattern The pattern with newly added cell.
     */
    fun onPatternCellAdded(pattern: ArrayList<Cell>, simplePattern: String?)

    /**
     * A pattern was detected from the user.
     *
     * @param pattern The pattern.
     */
    fun onPatternDetected(pattern: ArrayList<Cell>, simplePattern: String?)
}