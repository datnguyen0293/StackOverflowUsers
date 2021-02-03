package com.datnq.stack.overflow.users.application.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * @author dat nguyen
 * @since 2019 Sep 12
 */
class UserItem : Parcelable {
    @SerializedName("user_id")
    var userId: Long = -1

    @SerializedName("display_name")
    var userName: String = ""

    @SerializedName("profile_image")
    var userAvatar: String = ""

    @SerializedName("reputation")
    var reputation: Long = -1

    @SerializedName("last_access_date")
    var lastAccessDate: Long = -1

    @SerializedName("location")
    var location: String = ""
    var userAvatarBitmap: Bitmap? = null

    constructor() {
        userId = -1
        userName = ""
        userAvatar = ""
        reputation = -1
        lastAccessDate = -1
        location = ""
    }

    private constructor(parcel: Parcel) {
        userId = parcel.readLong()
        parcel.readString()?.let { userName = it }
        parcel.readString()?.let { userAvatar = it }
        reputation = parcel.readLong()
        lastAccessDate = parcel.readLong()
        parcel.readString()?.let { location = it }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(userId)
        dest.writeString(userName)
        dest.writeString(userAvatar)
        dest.writeLong(reputation)
        dest.writeLong(lastAccessDate)
        dest.writeString(location)
    }

    companion object {
        val CREATOR: Parcelable.Creator<UserItem> = object : Parcelable.Creator<UserItem> {
            override fun createFromParcel(parcel: Parcel): UserItem {
                return UserItem(parcel)
            }

            override fun newArray(size: Int): Array<UserItem> {
                return Array(size) { UserItem() }
            }
        }
    }
}