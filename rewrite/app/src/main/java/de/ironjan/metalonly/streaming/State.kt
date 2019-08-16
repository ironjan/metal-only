package de.ironjan.metalonly.streaming

import android.os.Parcel
import android.os.Parcelable
enum class State : Parcelable {
    Gone, Preparing, Started, Completed, Stopping, Error;

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeString(name)

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<State> {
        override fun createFromParcel(parcel: Parcel): State = valueOf(parcel.readString()!!)

        override fun newArray(size: Int): Array<State?> = arrayOfNulls(size)
    }
}

