package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteV2(@SerializedName("id") val id: String? = null,
                   @SerializedName("quote_type") val quoteType: QuoteType = QuoteType.ESTIMATED,
                   @SerializedName("source") val quoteSource: QuoteSource = QuoteSource.FLEET,
                   @SerializedName("price") val price: QuotePrice = QuotePrice(),
                   @SerializedName("fleet") val fleet: FleetInfo = FleetInfo(),
                   @SerializedName("pick_up_type") val pickupType: PickupType? = null,
                   @SerializedName("vehicle") val vehicle: QuoteVehicle = QuoteVehicle(),
                   @SerializedName("vehicle_attributes") val vehicleAttributes: VehicleAttributes = VehicleAttributes()) : Parcelable
