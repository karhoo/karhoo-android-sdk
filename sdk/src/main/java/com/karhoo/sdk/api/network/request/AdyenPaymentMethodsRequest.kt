package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.ANDROID
import com.karhoo.sdk.api.model.adyen.AdyenAmount
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentMethodsRequest(
        @SerializedName("amount") val amount: AdyenAmount? = AdyenAmount("GBP", 0.0),
        @SerializedName("channel") val string: String = ANDROID,
        @SerializedName("countryCode") val countryCode: String? = "GB",
        @SerializedName("shopperLocale") val shopperLocale: String? = "en-GB")
    : Parcelable