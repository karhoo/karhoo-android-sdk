package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.adyen.Amount
import com.karhoo.sdk.api.model.adyen.AdyenStoredPaymentMethod
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentsRequest(
        @SerializedName("amount") val amount: Amount,
        @SerializedName("merchantAccount") val merchantAccount: String,
        @SerializedName("paymentMethod") val paymentMethodAdyen: AdyenStoredPaymentMethod,
        @SerializedName("reference") val reference: String,
        @SerializedName("returnUrl") val returnUrl: String
                               ) : Parcelable