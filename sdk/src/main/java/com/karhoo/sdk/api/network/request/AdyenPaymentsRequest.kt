package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.adyen.AdyenAmount
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsRequestPayload
import com.karhoo.sdk.api.model.adyen.AdyenStoredPaymentMethod
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentsRequest(
        @SerializedName("payments_payload") val paymentsPayload: AdyenPaymentsRequestPayload,
        @SerializedName("return_url_suffix") val returnUrlSuffix: String = "https://myserver.com")
    : Parcelable