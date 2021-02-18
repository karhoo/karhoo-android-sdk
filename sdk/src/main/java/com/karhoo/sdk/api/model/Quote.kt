package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quote(@SerializedName("id") val id: String? = null,
                 @SerializedName("price") val price: QuotePrice = QuotePrice(),
                 @SerializedName("pick_up_type") val pickupType: PickupType? = null,
                 @SerializedName("quote_type") val quoteType: QuoteType = QuoteType.ESTIMATED,
                 @SerializedName("source") val quoteSource: QuoteSource = QuoteSource.FLEET,
                 @SerializedName("fleet") val fleet: FleetInfo = FleetInfo(),
                 @SerializedName("vehicle") val vehicle: QuoteVehicle = QuoteVehicle(),
                 @SerializedName("service_level_agreements") val serviceAgreements: ServiceAgreements? = null) : Parcelable
