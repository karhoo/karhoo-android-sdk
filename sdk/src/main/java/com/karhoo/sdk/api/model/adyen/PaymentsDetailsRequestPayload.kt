package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentsDetailsRequestPayload(
        @SerializedName("transactionID") val transactionID: String,
        @SerializedName("paymentsPayload") val paymentsPayload: AdyenPaymentsDetailsRequest) : Parcelable
