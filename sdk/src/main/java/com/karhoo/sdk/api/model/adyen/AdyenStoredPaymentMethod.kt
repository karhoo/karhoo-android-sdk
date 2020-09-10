package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenStoredPaymentMethod(
        @SerializedName("cvc") val cvc: String = "",
        @SerializedName("expiryMonth") val expiryMonth: String = "",
        @SerializedName("expiryYear") val expiryYear: String = "",
        @SerializedName("holderName") val holderName: String = "",
        @SerializedName("number") val number: String = "",
        @SerializedName("type") val type: String = "")
    : Parcelable
