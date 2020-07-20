package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteQTA(@SerializedName("high_minutes") val highMinutes: Int = 0,
                   @SerializedName("low_minutes") val lowMinutes: Int = 0) : Parcelable