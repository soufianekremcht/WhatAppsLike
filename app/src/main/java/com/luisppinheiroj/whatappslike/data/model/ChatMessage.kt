package com.luisppinheiroj.whatappslike.data.model

import android.os.Parcel
import android.os.Parcelable

data class ChatMessage(
    val messageId: String?, val body: String , val date: Long,
    val read: Boolean, var sender: UserObject,val participants: List<UserObject>, var isReceived: Boolean) :
    Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(UserObject::class.java.classLoader)!!,
        parcel.createTypedArrayList(UserObject)!!,
        parcel.readByte() != 0.toByte())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageId)
        parcel.writeString(body)
        parcel.writeLong(date)
        parcel.writeByte(if (read) 1 else 0)
        parcel.writeParcelable(sender, flags)
        parcel.writeTypedList(participants)
        parcel.writeByte(if (isReceived) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessage

        if (messageId != other.messageId) return false

        return true
    }

    override fun hashCode(): Int {
        return messageId?.hashCode() ?: 0
    }

    companion object CREATOR : Parcelable.Creator<ChatMessage> {
        override fun createFromParcel(parcel: Parcel): ChatMessage {
            return ChatMessage(parcel)
        }

        override fun newArray(size: Int): Array<ChatMessage?> {
            return arrayOfNulls(size)
        }
    }




}
