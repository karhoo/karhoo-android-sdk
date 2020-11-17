package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Passengers(@SerializedName("additional_passengers") val additionalPassengers: Int,
                      @SerializedName("passenger_details") val passengerDetails: List<PassengerDetails>?)
    : Parcelable

@Parcelize
data class PassengerDetails(@SerializedName("first_name") var firstName: String? = null,
                            @SerializedName("last_name") var lastName: String? = null,
                            @SerializedName("email") var email: String? = null,
                            @SerializedName("phone_number") var phoneNumber: String? = null,
                            @SerializedName("locale") var locale: String? = null)
    : Parcelable
