package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyConversion(@SerializedName("version") var version: String = "",
                             @SerializedName("rates") var rates: List<LoyaltyRates> = emptyList())
    : Parcelable
