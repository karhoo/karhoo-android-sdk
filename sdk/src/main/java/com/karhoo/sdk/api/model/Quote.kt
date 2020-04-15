package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Quote(@SerializedName("fleet_id") val fleetId: String? = null,
                 @SerializedName("quote_id") val quoteId: String? = null,
                 @SerializedName("vehicle_class") val vehicleClass: String? = null,
                 @SerializedName("quote_type") val quoteType: QuoteType = QuoteType.ESTIMATED,
                 @SerializedName("source") val quoteSource: QuoteSource = QuoteSource.FLEET,
                 @SerializedName("high_price") val highPrice: Int = 0,
                 @SerializedName("low_price") val lowPrice: Int = 0,
                 @SerializedName("currency_code") val currencyCode: String? = null,
                 @SerializedName("availability_id") val availabilityId: String? = null,
                 @SerializedName("fleet_name") val supplierName: String = "",
                 @SerializedName("supplier_logo_url") val logoUrl: String? = null,
                 @SerializedName("phone_number") val phoneNumber: String? = null,
                 @SerializedName("pick_up_type") val pickupType: PickupType? = null,
                 @SerializedName("qta_high_minutes") val qta: Int? = null,
                 @SerializedName("terms_conditions_url") val termsAndConditions: String = "",
                 @SerializedName("category_name") val categoryName: String = "",
                 @SerializedName("vehicle_attributes") val vehicleAttributes: VehicleAttributes = VehicleAttributes()) : Parcelable