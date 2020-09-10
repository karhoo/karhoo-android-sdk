package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenBillingAddress(
        @SerializedName("city") val city: String = "",
        @SerializedName("country") val country: String = "",
        @SerializedName("houseNumberOrName") val houseNumberOrName: String = "",
        @SerializedName("postalCode") val postalCode: String = "",
        @SerializedName("stateOrProvince") val stateOrProvince: String? = "",
        @SerializedName("street") val street: String = "")
    : Parcelable
