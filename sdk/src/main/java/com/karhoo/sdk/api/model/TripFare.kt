package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripFare(@SerializedName("total") val total: Int = 0,
                @SerializedName("currency") val currency: String = "",
                @SerializedName("gratuity_percent") val gratuityPercent: Int = 0,
                @SerializedName("breakdown") val breakdown: TripFareBreakdown? = TripFareBreakdown()) : Parcelable
