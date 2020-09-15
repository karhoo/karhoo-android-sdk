package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentsRequestPayload(
        @SerializedName("paymentsPayload") val paymentsPayload: AdyenPaymentsRequest,
        @SerializedName("returnUrlSuffix") val returnUrlSuffix: String) : Parcelable
