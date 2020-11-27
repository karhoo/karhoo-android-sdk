package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.network.request.QuoteQTA
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteVehicle(@SerializedName("qta") val vehicleQta: QuoteQTA = QuoteQTA(),
                        @SerializedName("class") val vehicleClass: String? = "",
                        @SerializedName("type") val vehicleType: String? = "",
                        @SerializedName("passenger_capacity") val passengerCapacity: Int = 0,
                        @SerializedName("luggage_capacity") val luggageCapacity: Int = 0,
                        @SerializedName("tags") val vehicleTags: List<String> = listOf()) : Parcelable
