package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenCard(
        @SerializedName("expiryMonth") val expiryMonth: String,
        @SerializedName("expiryYear") val expiryYear: String,
        @SerializedName("holderName") val holderName: String,
        @SerializedName("number") val number: String)
    : Parcelable
