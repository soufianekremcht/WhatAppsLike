package com.luisppinheiroj.whatappslike.data.model

import android.os.Parcel
import android.os.Parcelable


data class ChatObject(
    var chatId: String?,
    var snippet: String,
    var date: Long,
    var read: Boolean,
    var title: String,
    var photoUri: String?,
    var isGroupConversation: Boolean) : Parcelable {



    var participantUsers : ArrayList<UserObject> = ArrayList()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(snippet)
        parcel.writeLong(date)
        parcel.writeByte(if (read) 1 else 0)
        parcel.writeString(title)
        parcel.writeString(photoUri)
        parcel.writeByte(if (isGroupConversation) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatObject

        if (chatId != other.chatId) return false

        return true
    }

    override fun hashCode(): Int {
        return chatId?.hashCode() ?: 0
    }


    companion object CREATOR : Parcelable.Creator<ChatObject> {
        override fun createFromParcel(parcel: Parcel): ChatObject {
            return ChatObject(parcel)
        }

        override fun newArray(size: Int): Array<ChatObject?> {
            return arrayOfNulls(size)
        }
    }







}