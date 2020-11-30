package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FareBreakdown(@SerializedName("value") val value: Int = 0,
                         @SerializedName("name") val name: String = "",
                        @SerializedName("description") val description: String? = "") : Parcelable
