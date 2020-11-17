package com.karhoo.sdk.api.model

import android.os.Parcelable

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vehicle(@SerializedName("vehicle_class") val vehicleClass: String = "",
                   @SerializedName("description") val description: String = "",
                   @SerializedName("vehicle_license_plate") val vehicleLicencePlate: String = "",
                   @SerializedName("driver") val driver: Driver? = null) : Parcelable
