package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeetingPoint(
        @SerializedName("position") val position: Position? = null,
        @SerializedName("type") val pickupType: PickupType? = null,
        @SerializedName("instructions") val instructions: String = "")
    : Parcelable