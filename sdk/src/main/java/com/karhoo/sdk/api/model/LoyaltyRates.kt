package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyRates(@SerializedName("currency") var currency: String? = "",
                        @SerializedName("points") var points: Double? = null) : Parcelable
