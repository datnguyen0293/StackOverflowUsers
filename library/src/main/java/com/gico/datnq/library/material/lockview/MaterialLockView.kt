package com.gico.datnq.library.material.lockview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Debug
import android.os.SystemClock
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.gico.datnq.library.R
import com.gico.datnq.library.utilities.LoggerUtil
import java.util.*
import kotlin.math.*

@Suppress("DEPRECATION")
class MaterialLockView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    companion object {
        /**
         * The size of the pattern's matrix.
         */
        const val MATRIX_SIZE = Cell.LOCK_SIZE * Cell.LOCK_SIZE

        /**
         * How many milliseconds we spend animating each circle of a lock pattern if the animating mode is set. The entire
         * animation should take this constant * the length of the pattern to complete.
         */
        private const val MILLIS_PER_CIRCLE_ANIMATING = 700

        /**
         * This can be used to avoid updating the display for very small motions or noisy panels. It didn't seem to have
         * much impact on the devices tested, so currently set to 0.
         */
        private const val DRAG_THRESHOLD = 0.0f
        private const val PROFILE_DRAWING = false
    }

    private var mCellStates: Array<Array<CellState>> = emptyArray()
    private var mDotSize = 0
    private var mDotSizeActivated = 0
    private var mPathWidth = 0
    private var mCurrentPath = Path()
    private var mInvalidate = Rect()
    private var mTmpInvalidateRect = Rect()

    private var mDrawingProfilingStarted = false
    private var mPaint = Paint()
    private var mPathPaint = Paint()
    var onPatternListener: OnPatternListener? = null
    private var mPattern = ArrayList<Cell>(MATRIX_SIZE)

    /**
     * Lookup table for the circles of the pattern we are currently drawing. This will be the cells of the complete
     * pattern unless we are animating, in which case we use this to hold the cells we are drawing for the in progress
     * animation.
     */
    private var mPatternDrawLookup = Array(Cell.LOCK_SIZE) { BooleanArray(Cell.LOCK_SIZE) }

    /**
     * the in progress point: - during interaction: where the user's finger is - during animation: the current tip of
     * the animating line
     */
    private var mInProgressX = -1f
    private var mInProgressY = -1f
    private var mAnimatingPeriodStart: Long = 0
    private var mPatternDisplayMode = DisplayMode.CORRECT
    private var mInputEnabled = true
    private var mInStealthMode = false
    private var mEnableHapticFeedback = true
    private var mPatternInProgress = false
    private var mHitFactor = 0.6f
    private var mSquareWidth = 0f
    private var mSquareHeight = 0f
    private var mRegularColor = 0
    private var mErrorColor = 0
    private var mSuccessColor = 0
    private var mFastOutSlowInInterpolator: Interpolator? = null
    private var mLinearOutSlowInInterpolator: Interpolator? = null

    init {
        isClickable = true
        mPathPaint.isAntiAlias = true
        mPathPaint.isDither = true

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialLockView)
        mRegularColor = typedArray.getColor(R.styleable.MaterialLockView_LOCK_COLOR, Color.WHITE)
        mErrorColor = typedArray.getColor(R.styleable.MaterialLockView_WRONG_COLOR, Color.RED)
        mSuccessColor = typedArray.getColor(R.styleable.MaterialLockView_CORRECT_COLOR, Color.GREEN)
        typedArray.recycle()

        mPathPaint.color = mRegularColor
        mPathPaint.style = Paint.Style.STROKE
        mPathPaint.strokeJoin = Paint.Join.ROUND
        mPathPaint.strokeCap = Paint.Cap.ROUND

        mPathWidth = dpToPx(3f)
        mPathPaint.strokeWidth = mPathWidth.toFloat()
        mDotSize = dpToPx(12f)
        mDotSizeActivated = dpToPx(28f)
        mPaint.isAntiAlias = true
        mPaint.isDither = true

        mCellStates = Array(Cell.LOCK_SIZE) { Array(Cell.LOCK_SIZE) { CellState() } }
        for (i in 0 until Cell.LOCK_SIZE) {
            for (j in 0 until Cell.LOCK_SIZE) {
                mCellStates[i][j] = CellState()
                mCellStates[i][j].size = mDotSize.toFloat()
            }
        }

        if (!isInEditMode) {
            mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(
                    context, android.R.interpolator.fast_out_slow_in)
            mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(
                    context, android.R.interpolator.linear_out_slow_in)
        }
    }

    /**
     * Set the pattern explicitly (rather than waiting for the user to input a pattern).
     *
     * @param displayMode How to display the pattern.
     * @param pattern     The pattern.
     */
    fun setPattern(displayMode: DisplayMode, pattern: List<Cell>) {
        mPattern.clear()
        mPattern.addAll(pattern)
        clearPatternDrawLookup()
        for (cell in pattern) {
            mPatternDrawLookup[cell.row][cell.column] = true
        }
        setDisplayMode(displayMode)
    }

    /**
     * Gets display mode.
     *
     * @return display mode.
     */
    fun getDisplayMode(): DisplayMode? {
        return mPatternDisplayMode
    } // getDisplayMode()


    /**
     * Set the display mode of the current pattern. This can be useful, for instance, after detecting a pattern to tell
     * this view whether change the in progress result to correct or wrong.
     *
     * @param displayMode The display mode.
     */
    fun setDisplayMode(displayMode: DisplayMode) {
        mPatternDisplayMode = displayMode
        if (displayMode === DisplayMode.ANIMATE) {
            check(mPattern.size != 0) {
                ("you must have a pattern to "
                        + "animate if you want to set the display mode to animate")
            }
            mAnimatingPeriodStart = SystemClock.elapsedRealtime()
            val first = mPattern[0]
            mInProgressX = getCenterXForColumn(first.column)
            mInProgressY = getCenterYForRow(first.row)
            clearPatternDrawLookup()
        }
        invalidate()
    }

    private fun getSimplePattern(pattern: List<Cell>): String? {
        val stringBuilder = StringBuilder()
        for (cell in pattern) {
            stringBuilder.append(getSimpleCellPosition(cell))
        }
        return stringBuilder.toString()
    }

    private fun getSimpleCellPosition(cell: Cell?): String? {
        if (cell == null) return ""
        when (cell.row) {
            0 -> when (cell.column) {
                0 -> return "1"
                1 -> return "2"
                2 -> return "3"
            }
            1 -> when (cell.column) {
                0 -> return "4"
                1 -> return "5"
                2 -> return "6"
            }
            2 -> when (cell.column) {
                0 -> return "7"
                1 -> return "8"
                2 -> return "9"
            }
        }
        return ""
    }

    private fun notifyCellAdded() {
        onPatternListener?.onPatternCellAdded(mPattern, getSimplePattern(mPattern))
    }

    private fun notifyPatternStarted() {
        onPatternListener?.onPatternStart()
    }

    private fun notifyPatternDetected() {
        onPatternListener?.onPatternDetected(mPattern, getSimplePattern(mPattern))
    }

    private fun notifyPatternCleared() {
        onPatternListener?.onPatternCleared()
    }

    /**
     * Clear the pattern.
     */
    fun clearPattern() {
        resetPattern()
    }

    /**
     * Reset all pattern state.
     */
    private fun resetPattern() {
        mPattern.clear()
        clearPatternDrawLookup()
        mPatternDisplayMode = DisplayMode.CORRECT
        invalidate()
    }

    /**
     * Clear the pattern lookup table.
     */
    private fun clearPatternDrawLookup() {
        for (i in 0 until Cell.LOCK_SIZE) {
            for (j in 0 until Cell.LOCK_SIZE) {
                mPatternDrawLookup[i][j] = false
            }
        }
    }

    /**
     * Disable input (for instance when displaying a message that will timeout so user doesn't get view into messy
     * state).
     */
    fun disableInput() {
        mInputEnabled = false
    }

    /**
     * Enable input.
     */
    fun enableInput() {
        mInputEnabled = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val width = w - paddingLeft - paddingRight
        mSquareWidth = width / Cell.LOCK_SIZE.toFloat()
        val height = h - paddingTop - paddingBottom
        mSquareHeight = height / Cell.LOCK_SIZE.toFloat()
    }

    private fun resolveMeasured(measureSpec: Int, desired: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> desired
            MeasureSpec.AT_MOST -> max(specSize, desired)
            MeasureSpec.EXACTLY -> specSize
            else -> specSize
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth = suggestedMinimumWidth
        val minimumHeight = suggestedMinimumHeight
        var viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth)
        var viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight)
        viewHeight = min(viewWidth, viewHeight)
        viewWidth = viewHeight
        setMeasuredDimension(viewWidth, viewHeight)
    }

    /**
     * Determines whether the point x, y will add a new point to the current pattern (in addition to finding the cell,
     * also makes heuristic choices such as filling in gaps based on current pattern).
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun detectAndAddHit(x: Float, y: Float): Cell? {
        checkForNewHit(x, y)?.let { cell ->

            // check for gaps in existing pattern
            var fillInGapCell: Cell? = null
            val pattern = mPattern
            if (pattern.isNotEmpty()) {
                val lastCell = pattern[pattern.size - 1]
                val dRow = cell.row - lastCell.row
                val dColumn = cell.column - lastCell.column
                var fillInRow = lastCell.row
                var fillInColumn = lastCell.column
                if (abs(dRow) == 2 && abs(dColumn) != 1) {
                    fillInRow = lastCell.row + if (dRow > 0) 1 else -1
                }
                if (abs(dColumn) == 2 && abs(dRow) != 1) {
                    fillInColumn = lastCell.column + if (dColumn > 0) 1 else -1
                }
                fillInGapCell = Cell.of(fillInRow, fillInColumn)
            }
            fillInGapCell?.let {
                if (!mPatternDrawLookup[it.row][it.column]) {
                    addCellToPattern(it)
                }
            }
            addCellToPattern(cell)
            if (mEnableHapticFeedback) {
                performHapticFeedback(
                        HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                        or HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
            }
            return cell
        }
        return null
    }

    private fun addCellToPattern(newCell: Cell) {
        mPatternDrawLookup[newCell.row][newCell.column] = true
        mPattern.add(newCell)
        if (!mInStealthMode) {
            startCellActivatedAnimation(newCell)
        }
        notifyCellAdded()
    }

    private fun startCellActivatedAnimation(cell: Cell) {
        val cellState = mCellStates[cell.row][cell.column]
        mLinearOutSlowInInterpolator?.let {
            startSizeAnimation(mDotSize.toFloat(), mDotSizeActivated.toFloat(), 96,
                    it, cellState, {
                mFastOutSlowInInterpolator?.let { fast ->
                    startSizeAnimation(mDotSizeActivated.toFloat(), mDotSize.toFloat(), 192,
                            fast, cellState, null)
                }
            })
        }
        startLineEndAnimation(cellState, mInProgressX, mInProgressY,
                getCenterXForColumn(cell.column), getCenterYForRow(cell.row))
    }

    private fun startLineEndAnimation(state: CellState,
                                      startX: Float, startY: Float, targetX: Float,
                                      targetY: Float) {
        /*
         * Currently this animation looks unclear, we don't really need it...
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) return
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator
                .addUpdateListener { animation: ValueAnimator ->
                    val t = animation.animatedValue as Float
                    state.lineEndX = (1 - t) * startX + t * targetX
                    state.lineEndY = (1 - t) * startY + t * targetY
                    invalidate()
                }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                state.lineAnimator = null
            }
        })
        valueAnimator.interpolator = mFastOutSlowInInterpolator
        valueAnimator.duration = 100
        valueAnimator.start()
        state.lineAnimator = valueAnimator
    }

    private fun startSizeAnimation(start: Float, end: Float, duration: Long,
                                   interpolator: Interpolator, state: CellState,
                                   endRunnable: Runnable?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val animator = FloatAnimator(start, end, duration)
            animator.addEventListener(object : EventListener {
                override fun onAnimationCancel(animator: FloatAnimator) {
                    // Do nothing
                }

                override fun onAnimationStart(animator: FloatAnimator) {
                    // Do nothing
                }

                override fun onAnimationUpdate(animator: FloatAnimator) {
                    state.size = animator.animatedValue
                    invalidate()
                } // onAnimationUpdate()

                override fun onAnimationEnd(animator: FloatAnimator) {
                    endRunnable?.run()
                } // onAnimationEnd()
            })
            animator.start()
        } // API < 11
        else {
            val valueAnimator = ValueAnimator.ofFloat(start, end)
            valueAnimator
                    .addUpdateListener { animation: ValueAnimator ->
                        state.size = (animation.animatedValue as Float)
                        invalidate()
                    }
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    endRunnable?.run()
                }
            })

            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        } // API 11+
    } // startSizeAnimation()


    // helper method to find which cell a point maps to
    private fun checkForNewHit(x: Float, y: Float): Cell? {
        val rowHit = getRowHit(y)
        if (rowHit < 0) {
            return null
        }
        val columnHit = getColumnHit(x)
        if (columnHit < 0) {
            return null
        }
        return if (mPatternDrawLookup[rowHit][columnHit]) {
            null
        } else Cell.of(rowHit, columnHit)
    }

    /**
     * Helper method to find the row that y falls into.
     *
     * @param y The y coordinate
     * @return The row that y falls in, or -1 if it falls in no row.
     */
    private fun getRowHit(y: Float): Int {
        val squareHeight = mSquareHeight
        val hitSize = squareHeight * mHitFactor
        val offset = paddingTop + (squareHeight - hitSize) / 2f
        for (i in 0 until Cell.LOCK_SIZE) {
            val hitTop = offset + squareHeight * i
            if (y >= hitTop && y <= hitTop + hitSize) {
                return i
            }
        }
        return -1
    }

    /**
     * Helper method to find the column x fallis into.
     *
     * @param x The x coordinate.
     * @return The column that x falls in, or -1 if it falls in no column.
     */
    private fun getColumnHit(x: Float): Int {
        val squareWidth = mSquareWidth
        val hitSize = squareWidth * mHitFactor
        val offset = paddingLeft + (squareWidth - hitSize) / 2f
        for (i in 0 until Cell.LOCK_SIZE) {
            val hitLeft = offset + squareWidth * i
            if (x >= hitLeft && x <= hitLeft + hitSize) {
                return i
            }
        }
        return -1
    }

    override fun onHoverEvent(event: MotionEvent): Boolean {
        if ((context.getSystemService(
                        Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).isTouchExplorationEnabled) {
            val action = event.action
            when (action) {
                MotionEvent.ACTION_HOVER_ENTER -> event.action = MotionEvent.ACTION_DOWN
                MotionEvent.ACTION_HOVER_MOVE -> event.action = MotionEvent.ACTION_MOVE
                MotionEvent.ACTION_HOVER_EXIT -> event.action = MotionEvent.ACTION_UP
            }
            onTouchEvent(event)
            event.action = action
        }
        return super.onHoverEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mInputEnabled || !isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleActionDown(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                handleActionUp(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                handleActionMove(event)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                /*
             * Original source check for mPatternInProgress == true first before
             * calling next three lines. But if we do that, there will be
             * nothing happened when the user taps at empty area and releases
             * the finger. We want the pattern to be reset and the message will
             * be updated after the user did that.
             */mPatternInProgress = false
                resetPattern()
                notifyPatternCleared()
                if (PROFILE_DRAWING) {
                    if (mDrawingProfilingStarted) {
                        Debug.stopMethodTracing()
                        mDrawingProfilingStarted = false
                    }
                }
                return true
            }
        }
        return false
    }

    private fun handleActionMove(event: MotionEvent) {
        // Handle all recent motion events so we don't skip any cells even when
        // the device
        // is busy...
        val radius = mPathWidth.toFloat()
        val historySize = event.historySize
        mTmpInvalidateRect.setEmpty()
        var invalidateNow = false
        for (i in 0 until historySize + 1) {
            val x = if (i < historySize) event.getHistoricalX(i) else event
                    .x
            val y = if (i < historySize) event.getHistoricalY(i) else event
                    .y
            val hitCell = detectAndAddHit(x, y)
            val patternSize = mPattern.size
            hitCell?.let {
                if (patternSize == 1) {
                    mPatternInProgress = true
                    notifyPatternStarted()
                }
            }
            // note current x and y for rubber banding of in progress patterns
            val dx = abs(x - mInProgressX)
            val dy = abs(y - mInProgressY)
            if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
                invalidateNow = true
            }
            if (mPatternInProgress && patternSize > 0) {
                val pattern = mPattern
                val lastCell = pattern[patternSize - 1]
                val lastCellCenterX = getCenterXForColumn(lastCell.column)
                val lastCellCenterY = getCenterYForRow(lastCell.row)

                // Adjust for drawn segment from last cell to (x,y). Radius
                // accounts for line width.
                var left = min(lastCellCenterX, x) - radius
                var right = max(lastCellCenterX, x) + radius
                var top = min(lastCellCenterY, y) - radius
                var bottom = max(lastCellCenterY, y) + radius

                // Invalidate between the pattern's new cell and the pattern's
                // previous cell
                hitCell?.let {
                    val width = mSquareWidth * 0.5f
                    val height = mSquareHeight * 0.5f
                    val hitCellCenterX = getCenterXForColumn(it.column)
                    val hitCellCenterY = getCenterYForRow(it.row)
                    left = min(hitCellCenterX - width, left)
                    right = max(hitCellCenterX + width, right)
                    top = min(hitCellCenterY - height, top)
                    bottom = max(hitCellCenterY + height, bottom)
                }

                // Invalidate between the pattern's last cell and the previous
                // location
                mTmpInvalidateRect.union(round(left).toInt(), round(top).toInt(),
                        round(right).toInt(), round(bottom).toInt())
            }
        }
        mInProgressX = event.x
        mInProgressY = event.y

        // To save updates, we only invalidate if the user moved beyond a
        // certain amount.
        if (invalidateNow) {
            mInvalidate.union(mTmpInvalidateRect)
            invalidate(mInvalidate)
            mInvalidate.set(mTmpInvalidateRect)
        }
    }

    private fun sendAccessEvent(resId: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            contentDescription = context.getString(resId)
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
            contentDescription = null
        } else announceForAccessibility(context.getString(resId))
    }

    private fun handleActionUp(event: MotionEvent) {
        LoggerUtil.i(javaClass.simpleName, "handleActionUp(event: MotionEvent)", "Field not used: ${event.javaClass.simpleName}")
        // report pattern detected
        if (mPattern.isNotEmpty()) {
            mPatternInProgress = false
            cancelLineAnimations()
            notifyPatternDetected()
            invalidate()
        }
        if (PROFILE_DRAWING) {
            if (mDrawingProfilingStarted) {
                Debug.stopMethodTracing()
                mDrawingProfilingStarted = false
            }
        }
    }

    private fun cancelLineAnimations() {
        for (i in 0 until Cell.LOCK_SIZE) {
            for (j in 0 until Cell.LOCK_SIZE) {
                val state = mCellStates[i][j]
                state.lineAnimator?.cancel()
                state.lineEndX = Float.MIN_VALUE
                state.lineEndY = Float.MIN_VALUE
            }
        }
    }

    private fun handleActionDown(event: MotionEvent) {
        resetPattern()
        val x = event.x
        val y = event.y
        val hitCell = detectAndAddHit(x, y)
        hitCell?.let {
            mPatternInProgress = true
            mPatternDisplayMode = DisplayMode.CORRECT
            notifyPatternStarted()
        } ?: run {
            /*
             * Original source check for mPatternInProgress == true first before
             * calling this block. But if we do that, there will be nothing
             * happened when the user taps at empty area and releases the
             * finger. We want the pattern to be reset and the message will be
             * updated after the user did that.
             */
            mPatternInProgress = false
            notifyPatternCleared()
        }
        hitCell?.let {
            val startX = getCenterXForColumn(it.column)
            val startY = getCenterYForRow(it.row)
            val widthOffset = mSquareWidth / 2f
            val heightOffset = mSquareHeight / 2f
            invalidate((startX - widthOffset).toInt(),
                    (startY - heightOffset).toInt(),
                    (startX + widthOffset).toInt(), (startY + heightOffset).toInt())
        }
        mInProgressX = x
        mInProgressY = y
        if (PROFILE_DRAWING) {
            if (!mDrawingProfilingStarted) {
                Debug.startMethodTracing("LockPatternDrawing")
                mDrawingProfilingStarted = true
            }
        }
    }

    private fun getCenterXForColumn(column: Int): Float {
        return paddingLeft + column * mSquareWidth + mSquareWidth / 2f
    }

    private fun getCenterYForRow(row: Int): Float {
        return paddingTop + row * mSquareHeight + mSquareHeight / 2f
    }

    override fun onDraw(canvas: Canvas) {
        val pattern = mPattern
        val count = pattern.size
        val drawLookup = mPatternDrawLookup
        if (mPatternDisplayMode === DisplayMode.ANIMATE) {

            // figure out which circles to draw

            // + 1 so we pause on complete pattern
            val oneCycle = (count + 1) * MILLIS_PER_CIRCLE_ANIMATING
            val spotInCycle = ((SystemClock.elapsedRealtime() - mAnimatingPeriodStart).toInt()
                    % oneCycle)
            val numCircles = spotInCycle / MILLIS_PER_CIRCLE_ANIMATING
            clearPatternDrawLookup()
            for (i in 0 until numCircles) {
                val cell = pattern[i]
                drawLookup[cell.row][cell.column] = true
            }

            // figure out in progress portion of ghosting line
            val needToUpdateInProgressPoint = (numCircles in 1 until count)
            if (needToUpdateInProgressPoint) {
                val percentageOfNextCircle = ((spotInCycle % MILLIS_PER_CIRCLE_ANIMATING).toFloat()
                        / MILLIS_PER_CIRCLE_ANIMATING)
                val currentCell = pattern[numCircles - 1]
                val centerX = getCenterXForColumn(currentCell.column)
                val centerY = getCenterYForRow(currentCell.row)
                val nextCell = pattern[numCircles]
                val dx = (percentageOfNextCircle
                        * (getCenterXForColumn(nextCell.column) - centerX))
                val dy = (percentageOfNextCircle
                        * (getCenterYForRow(nextCell.row) - centerY))
                mInProgressX = centerX + dx
                mInProgressY = centerY + dy
            }
            invalidate()
        }
        val currentPath = mCurrentPath
        currentPath.rewind()

        // draw the circles
        for (i in 0 until Cell.LOCK_SIZE) {
            val centerY = getCenterYForRow(i)
            for (j in 0 until Cell.LOCK_SIZE) {
                val cellState = mCellStates[i][j]
                val centerX = getCenterXForColumn(j)
                val size = cellState.size * CellState.SCALE
                val translationY = CellState.TRANSLATE_Y
                drawCircle(canvas, centerX, centerY.toInt() + translationY,
                        size, drawLookup[i][j], CellState.ALPHA)
            }
        }

        // a cell
        // only the last segment of the path should be computed here
        // draw the path of the pattern (unless we are in stealth mode)
        val drawPath = !mInStealthMode
        if (drawPath) {
            mPathPaint.color = getCurrentColor(true /* partOfPattern */)
            var anyCircles = false
            var lastX = 0f
            var lastY = 0f
            for (i in 0 until count) {
                val cell = pattern[i]

                // only draw the part of the pattern stored in
                // the lookup table (this is only different in the case
                // of animation).
                if (!drawLookup[cell.row][cell.column]) {
                    break
                }
                anyCircles = true
                val centerX = getCenterXForColumn(cell.column)
                val centerY = getCenterYForRow(cell.row)
                if (i != 0) {
                    val state = mCellStates[cell.row][cell.column]
                    currentPath.rewind()
                    currentPath.moveTo(lastX, lastY)
                    if (state.lineEndX != Float.MIN_VALUE
                            && state.lineEndY != Float.MIN_VALUE) {
                        currentPath.lineTo(state.lineEndX, state.lineEndY)
                    } else {
                        currentPath.lineTo(centerX, centerY)
                    }
                    canvas.drawPath(currentPath, mPathPaint)
                }
                lastX = centerX
                lastY = centerY
            }

            // draw last in progress section
            if ((mPatternInProgress || mPatternDisplayMode === DisplayMode.ANIMATE)
                    && anyCircles) {
                currentPath.rewind()
                currentPath.moveTo(lastX, lastY)
                currentPath.lineTo(mInProgressX, mInProgressY)
                mPathPaint.alpha = (calculateLastSegmentAlpha(
                        mInProgressX, mInProgressY, lastX, lastY) * 255f).toInt()
                canvas.drawPath(currentPath, mPathPaint)
            }
        }
    }

    private fun calculateLastSegmentAlpha(x: Float, y: Float, lastX: Float,
                                          lastY: Float): Float {
        val diffX = x - lastX
        val diffY = y - lastY
        val dist = sqrt(diffX * diffX + diffY * diffY.toDouble()).toFloat()
        val frac = dist / mSquareWidth
        return min(1f, max(0f, (frac - 0.3f) * 4f))
    }

    private fun getCurrentColor(partOfPattern: Boolean): Int {
        return if (!partOfPattern || mInStealthMode || mPatternInProgress) {
            // unselected circle
            mRegularColor
        } else if (mPatternDisplayMode === DisplayMode.WRONG) {
            // the pattern is wrong
            mErrorColor
        } else if (mPatternDisplayMode === DisplayMode.CORRECT
                || mPatternDisplayMode === DisplayMode.ANIMATE) {
            mSuccessColor
        } else {
            throw IllegalStateException("unknown display mode "
                    + mPatternDisplayMode)
        }
    }

    /**
     * @param partOfPattern Whether this circle is part of the pattern.
     */
    private fun drawCircle(canvas: Canvas, centerX: Float, centerY: Float,
                           size: Float, partOfPattern: Boolean, alpha: Float) {
        mPaint.color = getCurrentColor(partOfPattern)
        mPaint.alpha = (alpha * 255).toInt()
        canvas.drawCircle(centerX, centerY, size / 2, mPaint)
    }

    private fun dpToPx(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}