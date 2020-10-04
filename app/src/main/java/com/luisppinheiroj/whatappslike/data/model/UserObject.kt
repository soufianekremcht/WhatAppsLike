package com.luisppinheiroj.whatappslike.data.model

import android.os.Parcel
import android.os.Parcelable

data class UserObject(var uid: String, var email: String, var name: String) :Parcelable{
     var isSelected = false

     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readString()!!,
          parcel.readString()!!) {
          isSelected = parcel.readByte() != 0.toByte()
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(uid)
          parcel.writeString(email)
          parcel.writeString(name)
          parcel.writeByte(if (isSelected) 1 else 0)
     }

     override fun describeContents(): Int {
          return 0
     }

     override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (javaClass != other?.javaClass) return false

          other as UserObject

          if (uid != other.uid) return false
          if (name != other.name) return false

          return true
     }

     override fun hashCode(): Int {
          var result = uid.hashCode()
          result = 31 * result + name.hashCode()
          return result
     }


     companion object CREATOR : Parcelable.Creator<UserObject> {
          override fun createFromParcel(parcel: Parcel): UserObject {
               return UserObject(parcel)
          }

          override fun newArray(size: Int): Array<UserObject?> {
               return arrayOfNulls(size)
          }
     }




}