package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FleetInfo(@SerializedName("fleet_id") var fleetId: String? = null,
                     @SerializedName("name") var name: String? = null,
                     @SerializedName("description") var description: String? = null,
                     @SerializedName("rating") var rating: FleetRating? = null,
                     @SerializedName("logo_url") var logoUrl: String? = null,
                     @SerializedName("terms_conditions_url") var termsConditionsUrl: String? = null,
                     @SerializedName("phone_number") var phoneNumber: String? = null,
                     @SerializedName("capabilities") var capabilities: List<String> = listOf())
    : Parcelable
