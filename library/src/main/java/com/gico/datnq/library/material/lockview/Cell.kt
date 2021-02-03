package com.datnq.stack.overflow.users.datnq.library.material.lockview

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Cell : Parcelable {
    @JvmField
    val row: Int
    @JvmField
    val column: Int

    companion object {
        /**
         * author: Aman Tonk
         */
        const val LOCK_SIZE = 3
        private var sCells = Array(LOCK_SIZE) { arrayOfNulls<Cell>(LOCK_SIZE) }

        /**
         * @param row    The row of the cell.
         * @param column The column of the cell.
         */
        @Synchronized @JvmStatic
        fun of(row: Int, column: Int): Cell? {
            checkRange(row, column)
            return sCells[row][column]
        }

        /**
         * Gets a cell from its ID.
         *
         * @param id the cell ID.
         * @return the cell.
         * @author Hai Bison
         * @since v2.7 beta
         */
        @Synchronized
        fun of(id: Int): Cell? {
            return of(id / LOCK_SIZE, id % LOCK_SIZE)
        }

        private fun checkRange(row: Int, column: Int) {
            require(!(row < 0 || row > LOCK_SIZE - 1)) {
                ("row must be in range 0-"
                        + (LOCK_SIZE - 1))
            }
            require(!(column < 0 || column > LOCK_SIZE - 1)) {
                ("column must be in range 0-"
                        + (LOCK_SIZE - 1))
            }
        }

        val CREATOR: Parcelable.Creator<Cell?> = object : Parcelable.Creator<Cell?> {
            override fun createFromParcel(parcelIn: Parcel): Cell? {
                return Cell(parcelIn)
            }

            override fun newArray(size: Int): Array<Cell?> {
                return arrayOfNulls(size)
            }
        }

        init {
            for (i in 0 until LOCK_SIZE) {
                for (j in 0 until LOCK_SIZE) {
                    sCells[i][j] = Cell(i, j)
                }
            }
        }
    }

    /**
     * @param row    number or row
     * @param column number of column
     */
    private constructor(row: Int, column: Int) {
        checkRange(row, column)
        this.row = row
        this.column = column
    }

    /**
     * Gets the ID.It is counted from left to right, top to bottom of the matrix, starting by zero.
     *
     * @return the ID.
     */
    val id: Int
        get() = row * LOCK_SIZE + column

    /**
     * @return Row and Column in String.
     */
    override fun toString(): String {
        return "(ROW=$row,COL=$column)"
    }

    override fun hashCode(): Int {
        return Objects.hash(row, column)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Cell) column == other.column
                && row == other.row else super.equals(other)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(column)
        dest.writeInt(row)
    }

    private constructor(parcelIn: Parcel) {
        column = parcelIn.readInt()
        row = parcelIn.readInt()
    }
}