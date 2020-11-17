package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenAmount(
        @SerializedName("currency") val currency: String = "",
        @SerializedName("value") val value: Int = 0)
    : Parcelable
