package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FareBreakdown(@SerializedName("total") val value: Int = 0,
                         @SerializedName("currency") val name: String = "") : Parcelable
