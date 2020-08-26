package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPayments(
        @SerializedName("amount") val amount: Amount = Amount(),
        @SerializedName("merchantReference") val merchantReference: String = "",
        @SerializedName("pspReference") val pspReference: String = "",
        @SerializedName("resultCode") val resultCode: String = "")
    : Parcelable