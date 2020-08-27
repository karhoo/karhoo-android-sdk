package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenBillingAddress(
        @SerializedName("action") val city: String = "",
        @SerializedName("action") val country: String = "",
        @SerializedName("action") val houseNumberOrName: String = "",
        @SerializedName("action") val postalCode: String = "",
        @SerializedName("action") val stateOrProvince: String? = "",
        @SerializedName("action") val street: String = "")
    : Parcelable