package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotePriceNet(@SerializedName("high") val highPrice: Int = 0,
                         @SerializedName("low") val lowPrice: Int = 0) : Parcelable
