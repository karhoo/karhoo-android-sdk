package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeetingPoint(
        @SerializedName("position") val position: Position? = null,
        @SerializedName("type") val pickupType: PickupType? = PickupType.NOT_SET,
        @SerializedName("instructions") val instructions: String = "")
    : Parcelable