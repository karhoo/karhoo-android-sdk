package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Availability(@SerializedName("availability_id") val availabilityId: String = "",
                        @SerializedName("fleet_id") val fleetId: String = "",
                        @SerializedName("vehicle_class") val vehicleClass: String = "",
                        @SerializedName("supplier_name") val supplierName: String = "",
                        @SerializedName("logo_url") val logoUrl: String = "",
                        @SerializedName("description") val description: String = "",
                        @SerializedName("phone_number") val phoneNumber: String = "",
                        @SerializedName("rating") val rating: Float = 0F,
                        @SerializedName("terms_conditions_url") val termsAndCondition: String = "",
                        @SerializedName("category_name") val categoryName: String = "") : Parcelable