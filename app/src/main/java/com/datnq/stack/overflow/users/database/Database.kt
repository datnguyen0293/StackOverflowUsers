package com.datnq.stack.overflow.users.database

import android.content.ContentValues
import android.database.Cursor
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.core.Utilities

class Database {

    companion object {
        /**
         * Version number to upgrade database version
         * each time if you Add, Edit table, you need to change the version number.
         */
        const val DATABASE_VERSION = 1

        /**
         * Database Name
         */
        const val DATABASE_NAME = "StackOverflowUser.db"

        /**
         * Table
         */
        const val TABLE_NAME = "User"
        const val COLUMN_USER_ID = "UserId"
        private const val COLUMN_USER_NAME = "UserName"
        private const val COLUMN_USER_AVATAR = "UserAvatar"
        private const val COLUMN_REPUTATION = "Reputation"
        private const val COLUMN_LAST_ACCESS_DATE = "LastAccessDate"
        private const val COLUMN_LOCATION = "Location"

        const val CREATE_TABLE_QUERY =
            "CREATE TABLE $TABLE_NAME ($COLUMN_USER_ID LONG PRIMARY KEY, $COLUMN_USER_NAME TEXT DEFAULT(\"\"), $COLUMN_USER_AVATAR BLOB, $COLUMN_REPUTATION LONG DEFAULT(0), $COLUMN_LAST_ACCESS_DATE LONG DEFAULT(0), $COLUMN_LOCATION TEXT DEFAULT(\"\")"
        const val UPDATE_DATABASE = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SELECT_QUERY = "SELECT * FROM $TABLE_NAME"

        @JvmStatic
        fun selectByCondition(id: Long): String {
            return "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USER_ID = $id"
        }

        @JvmStatic
        fun fromUserItem(userItem: UserItem): ContentValues {
            val values = ContentValues()
            values.put(COLUMN_USER_ID, userItem.userId)
            values.put(COLUMN_USER_NAME, userItem.userName)
            values.put(COLUMN_USER_AVATAR, Utilities.toBytes(userItem.userAvatar))
            values.put(COLUMN_REPUTATION, userItem.reputation)
            values.put(COLUMN_LAST_ACCESS_DATE, userItem.lastAccessDate)
            values.put(COLUMN_LOCATION, userItem.location)
            return values
        }

        @JvmStatic
        fun toUserItem(cursor: Cursor): UserItem {
            val userItem = UserItem()
            userItem.userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID))
            userItem.userName = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME))
            userItem.userAvatarBitmap = Utilities.toBitmap(
                cursor.getBlob(
                    cursor.getColumnIndex(
                        COLUMN_USER_AVATAR
                    )
                )
            )
            userItem.reputation = cursor.getLong(cursor.getColumnIndex(COLUMN_REPUTATION))
            userItem.lastAccessDate = cursor.getLong(
                cursor.getColumnIndex(
                    COLUMN_LAST_ACCESS_DATE
                )
            )
            userItem.location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION))
            return userItem
        }
    }
}