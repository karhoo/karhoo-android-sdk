package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VehicleAttributes(@SerializedName("passenger_capacity") val passengerCapacity: Int = 0,
                             @SerializedName("luggage_capacity") val luggageCapacity: Int = 0) : Parcelable