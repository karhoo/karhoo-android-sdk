package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FleetRating(@SerializedName("count") val count: Int = 0,
                       @SerializedName("score") val score: Int = 0) : Parcelable
