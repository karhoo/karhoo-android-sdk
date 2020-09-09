package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenBank(
        @SerializedName("bic") val bic: String,
        @SerializedName("countryCode") val countryCode: String,
        @SerializedName("iban") val iban: String,
        @SerializedName("ownerName") val ownerName: String)
    : Parcelable