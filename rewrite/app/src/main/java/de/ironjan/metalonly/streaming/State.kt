package de.ironjan.metalonly.streaming

import android.os.Parcel
import android.os.Parcelable

/** Represents streaming state. Wraps the internal media player states. */
enum class State : Parcelable {
    /** Stream is stopped. */
    Gone,
    /** Stream is being prepared. Corresponds to media player states Idle ... Preparing. */
    Preparing,
    /** Stream is currently playing. */
    Started,
    /** Stream is completed. TODO find out what this state means */
    Completed,
    /** Stream is stopping and service is cleaning up. */
    Stopping,
    /** An error occurred while playing the stream. */
    Error,
    /** Stream is prepared and paused */
    Paused;

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeString(name)

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<State> {
        override fun createFromParcel(parcel: Parcel): State = valueOf(parcel.readString()!!)

        override fun newArray(size: Int): Array<State?> = arrayOfNulls(size)
    }
}

