package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookingFeePrice(@SerializedName("currency") val currency: String? = "",
                           @SerializedName("type") val type: String? = "",
                           @SerializedName("value") val value: Int = 0) : Parcelable
