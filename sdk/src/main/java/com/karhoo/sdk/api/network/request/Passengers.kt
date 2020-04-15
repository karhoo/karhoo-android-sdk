package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Passengers(@SerializedName("additional_passengers") private val additionalPassengers: Int,
                      @SerializedName("passenger_details") private val passengerDetails: List<PassengerDetails>?)
    : Parcelable

@Parcelize
data class PassengerDetails(@SerializedName("first_name") private var firstName: String? = null,
                            @SerializedName("last_name") private var lastName: String? = null,
                            @SerializedName("email") private var email: String? = null,
                            @SerializedName("phone_number") private var phoneNumber: String? = null,
                            @SerializedName("locale") private var locale: String? = null)
    : Parcelable