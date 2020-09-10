package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
        @SerializedName("display_address") val displayAddress: String = "",
        @SerializedName("line_1") val lineOne: String = "",
        @SerializedName("line_2") val lineTwo: String = "",
        @SerializedName("building_number") val buildingNumber: String = "",
        @SerializedName("street_name") val streetName: String = "",
        @SerializedName("city") val city: String = "",
        @SerializedName("postal_code") val postalCode: String = "",
        @SerializedName("postal_code_ext") val postalCodeExt: String = "",
        @SerializedName("region") val region: String = "",
        @SerializedName("country_code") val countryCode: String = "")
    : Parcelable
