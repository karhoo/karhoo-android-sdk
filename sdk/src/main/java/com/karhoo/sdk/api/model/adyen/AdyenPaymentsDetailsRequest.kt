package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentsDetailsRequest(
        @SerializedName("details") val details: AdyenPaymentDetails,
        @SerializedName("paymentData") val paymentData: String,
        @SerializedName("threeDSAuthenticationOnly") val threeDSAuthenticationOnly: Boolean)
    : Parcelable
