package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripBooking(@SerializedName("comments") val comments: String? = null,
                       @SerializedName("flight_number") val flightNumber: String? = null,
                       @SerializedName("meta") val meta: Map<String, String>? = null,
                       @SerializedName("passengers") val passengers: Passengers? = null,
                       @SerializedName("payment_nonce") val nonce: String? = null,
                       @SerializedName("quote_id") val quoteId: String?) : Parcelable



