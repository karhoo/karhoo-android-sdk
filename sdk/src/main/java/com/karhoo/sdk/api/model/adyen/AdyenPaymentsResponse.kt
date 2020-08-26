package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentsResponse(
        @SerializedName("payload") val payload: AdyenPayments = AdyenPayments(),
        @SerializedName("transaction_id") val transactionId: Int = 0)
    : Parcelable