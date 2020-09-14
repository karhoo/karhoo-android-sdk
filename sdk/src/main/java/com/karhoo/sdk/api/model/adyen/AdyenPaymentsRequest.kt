package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.adyen.AdyenAmount
import com.karhoo.sdk.api.model.adyen.AdyenStoredPaymentMethod
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentsRequest(
        @SerializedName("amount") val amount: AdyenAmount,
        @SerializedName("merchantAccount") val merchantAccount: String,
        @SerializedName("paymentMethod") val storedPaymentMethod: AdyenStoredPaymentMethod,
        @SerializedName("reference") val reference: String,
        @SerializedName("returnUrl") val returnUrl: String)
    : Parcelable
