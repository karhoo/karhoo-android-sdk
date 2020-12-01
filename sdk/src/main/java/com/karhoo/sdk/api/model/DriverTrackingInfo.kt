package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DriverTrackingInfo(@SerializedName("position") val position: Position?,
                              @SerializedName("direction") val direction: Direction?,
                              @SerializedName("origin_eta") val originEta: Int = 0,
                              @SerializedName("destination_eta") val destinationEta: Int = 0) : Parcelable
