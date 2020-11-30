package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fare(@SerializedName("state") val state: String = "",
                @SerializedName("breakdown") val breakdown: FareBreakdown? = FareBreakdown()) : Parcelable
