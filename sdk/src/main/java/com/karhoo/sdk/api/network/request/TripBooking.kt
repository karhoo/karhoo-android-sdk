package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripBooking(@SerializedName("comments") private val comments: String? = null,
                       @SerializedName("flight_number") private val flightNumber: String? = null,
                       @SerializedName("meta") private val meta: HashMap<String, String>? = null,
                       @SerializedName("passengers") private val passengers: Passengers? = null,
                       @SerializedName("payment_nonce") val nonce: String? = null,
                       @SerializedName("quote_id") private val quoteId: String?) : Parcelable


