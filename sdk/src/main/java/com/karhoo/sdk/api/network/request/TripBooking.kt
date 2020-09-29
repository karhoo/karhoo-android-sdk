package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripBooking(@SerializedName("quote_id") private val quoteId: String?,
                       @SerializedName("passengers") private val passengers: Passengers?,
                       @SerializedName("flight_number") private val flightNumber: String? = null,
                       @SerializedName("comments") private val comments: String? = null,
                       @SerializedName("payment_nonce") val nonce: String? = null) : Parcelable


