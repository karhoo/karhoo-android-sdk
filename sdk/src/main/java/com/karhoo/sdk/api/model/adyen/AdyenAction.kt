package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenAction(
        @SerializedName("data") val data: AdyenData = AdyenData(),
        @SerializedName("method") val method: String = "",
        @SerializedName("paymentData") val paymentData: String = "",
        @SerializedName("paymentMethodType") val paymentMethodType: String = "",
        @SerializedName("type") val type: String = "",
        @SerializedName("url") val url: String = "")
    : Parcelable
