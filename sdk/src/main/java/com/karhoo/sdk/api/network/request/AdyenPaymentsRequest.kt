package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsRequest
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentsRequest(
        @SerializedName("payments_payload") val paymentsPayload: AdyenPaymentsRequest,
        @SerializedName("return_url_suffix") val returnUrlSuffix: String = "https://myserver.com")
    : Parcelable
