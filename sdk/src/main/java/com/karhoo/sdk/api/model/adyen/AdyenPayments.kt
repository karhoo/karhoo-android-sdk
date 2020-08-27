package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.ANDROID
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPayments(
        @SerializedName("action") val action: AdyenAction? = AdyenAction(),
        @SerializedName("amount") val amount: AdyenAmount? = AdyenAmount(),
        @SerializedName("billing") val billing: AdyenBillingAddress = AdyenBillingAddress(),
        @SerializedName("channel") val channel: String? = ANDROID,
        @SerializedName("details") val details: List<AdyenPaymentDetail>? = emptyList(),
        @SerializedName("enableOneClick") val enableOneClick: Boolean? = false,
        @SerializedName("enablePayOut") val enablePayOut: Boolean? = false,
        @SerializedName("merchantReference") val merchantReference: String? = "",
        @SerializedName("paymentData") val paymentData: String? = "",
        @SerializedName("pspReference") val pspReference: String? = "",
        @SerializedName("redirect") val redirect: AdyenRedirect? = AdyenRedirect(),
        @SerializedName("resultCode") val resultCode: String? = "")
    : Parcelable