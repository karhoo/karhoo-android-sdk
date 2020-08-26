package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Amount(
        @SerializedName("value") val currency: String = "",
        @SerializedName("value") val value: Double = 0.0)
    : Parcelable