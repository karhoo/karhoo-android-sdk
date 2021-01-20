package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripBooking(@SerializedName("comments") val comments: String? = null,
                       @SerializedName("flight_number") val flightNumber: String? = null,
                       @SerializedName("train_number") val trainNumber: String? = null,
                       @SerializedName("meta") val meta: Map<String, String>? = null,
                       @SerializedName("passengers") val passengers: Passengers? = null,
                       @SerializedName("partner_trip_id") val partnerTripId: String? = null,
                       @SerializedName("payment_nonce") val nonce: String? = null,
                       @SerializedName("cost_center_reference") val costReference: String? = null,
                       @SerializedName("train_time") val trainTime: String? = null,
                       @SerializedName("loyalty_programme") val loyaltyProgrammeId: String? = null,
                       @SerializedName("loyalty_points") val loyaltyPoints: Int = 0,
                       @SerializedName("quote_id") val quoteId: String?) : Parcelable


