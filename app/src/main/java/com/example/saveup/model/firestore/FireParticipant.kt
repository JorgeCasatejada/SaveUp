package com.example.saveup.model.firestore

import android.os.Parcel
import android.os.Parcelable

data class FireParticipant(
    val id: String,
    val email: String,
    val username: String,
    val imagePath: String,
    @field:JvmField var isAdmin: Boolean
): Parcelable {

    constructor() : this("", "", "", "", false)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(email)
        parcel.writeString(username)
        parcel.writeString(imagePath)
        parcel.writeByte(if (isAdmin) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FireParticipant> {
        override fun createFromParcel(parcel: Parcel): FireParticipant {
            return FireParticipant(parcel)
        }

        override fun newArray(size: Int): Array<FireParticipant?> {
            return arrayOfNulls(size)
        }
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FireParticipant
        if (email != other.email) return false
        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}
