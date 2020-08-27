package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenRedirect(
        @SerializedName("data") val data: AdyenData = AdyenData(),
        @SerializedName("method") val method: String = "",
        @SerializedName("url") val url: String = "")
    : Parcelable