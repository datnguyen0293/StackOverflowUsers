package com.datnq.stack.overflow.users.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.datnq.stack.overflow.users.application.model.UserItem
import com.datnq.stack.overflow.users.core.LoggerUtil
import java.io.IOException
import java.util.*

/**
 * @author dat nguyen
 * @since 2020 Feb 22
 */
class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION) {

    fun getFavoriteUsers(): ArrayList<UserItem> {
        //Open connection to read only
        val database = readableDatabase
        val userItemList = ArrayList<UserItem>()
        var cursor: Cursor? = null
        try {
            database.beginTransaction()
            cursor = database.rawQuery(Database.SELECT_QUERY, null)
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    userItemList.add(Database.toUserItem(cursor))
                } while (cursor.moveToNext())
            }
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            LoggerUtil.e(SQLiteHelper::class.java.simpleName, "getFavoriteUsers()", e)
        } finally {
            cursor?.close()
            database.endTransaction()
            database.close()
        }
        return userItemList
    }

    @Throws(Exception::class)
    fun updateFavoriteUsers(userItem: UserItem) {
        //Open connection to write data
        val database = writableDatabase
        database.beginTransaction()
        val cursor = database.rawQuery(Database.selectByCondition(userItem.userId), null)
        if (cursor.count > 0) {
            database.delete(
                Database.TABLE_NAME,
                Database.COLUMN_USER_ID + "= ?",
                arrayOf(userItem.userId.toString())
            )
        } else {
            val values = Database.fromUserItem(userItem)
            // Inserting Row
            database.insert(Database.TABLE_NAME, null, values)
        }
        database.setTransactionSuccessful()
        cursor.close()
        database.endTransaction()
        database.close() // Closing database connection
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(Database.CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            // Drop older table if existed, all data will be gone!
            database.execSQL(Database.UPDATE_DATABASE)
            // Create tables again
            onCreate(database)
        }
    }

}