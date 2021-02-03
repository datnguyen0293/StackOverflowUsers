package com.datnq.stack.overflow.users.application.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class Reputation() : Parcelable {
    @SerializedName("reputation_history_type")
    var reputationHistoryType: String = ""

    @SerializedName("reputation_change")
    var reputationChange: Int = 0

    @SerializedName("post_id")
    var postId: Long = 0

    @SerializedName("creation_date")
    var creationDate: Long = 0

    @SerializedName("user_id")
    private var userId: Long = 0

    constructor(parcel: Parcel) : this() {
        parcel.readString()?.let { reputationHistoryType = it }
        reputationChange = parcel.readInt()
        postId = parcel.readLong()
        creationDate = parcel.readLong()
        userId = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reputationHistoryType)
        parcel.writeInt(reputationChange)
        parcel.writeLong(postId)
        parcel.writeLong(creationDate)
        parcel.writeLong(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reputation> {
        override fun createFromParcel(parcel: Parcel): Reputation {
            return Reputation(parcel)
        }

        override fun newArray(size: Int): Array<Reputation?> {
            return arrayOfNulls(size)
        }
    }

}