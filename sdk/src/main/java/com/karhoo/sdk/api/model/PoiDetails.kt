package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PoiDetails(
        @SerializedName("iata") val iata: String = "",
        @SerializedName("terminal") val terminal: String = "",
        @SerializedName("type") val type: PoiDetailsType? = null)
    : Parcelable
