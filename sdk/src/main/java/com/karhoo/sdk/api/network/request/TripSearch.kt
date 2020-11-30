package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.TripStatus
import com.karhoo.sdk.api.model.TripType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripSearch(@SerializedName("trip_states") val tripState: List<TripStatus>? = null,
                      @SerializedName("trip_type") val tripType: String? = null,
                      @SerializedName("created_after") val createdAfter: String? = null,
                      @SerializedName("email") val email: String? = null,
                      @SerializedName("partner_traveller_id") val partnerTravellerId: String? = null,
                      @SerializedName("prebook_time_after") val prebookTimeAfter: String? = null,
                      @SerializedName("prebook_time_before") val prebookTimeBefore: String? = null,
                      @SerializedName("trip_time_before") val tripTimeBefore: String? = null,
                      @SerializedName("trip_time_after") val tripTimeAfter: String? = null,
                      @SerializedName("only_without_final_price") val withoutFinalPrice: Boolean? = false,
                      @SerializedName("pagination_offset") val paginationOffset: Int = 0,
                      @SerializedName("pagination_row_count") val paginationRowCount: Int = 0,
                      @SerializedName("fleet_id") val fleetId: String? = "",
                      @SerializedName("partner_trip_id") val partnerTripId: String? = null,
                      @SerializedName("external_trip_id") val externalId: String? = null,
                      @SerializedName("forename") val forename: String? = null,
                      @SerializedName("lastname") val lastname: String? = null,
                      @SerializedName("display_trip_id") val displayId: String? = "",
                      @SerializedName("order_by") val orderBy: List<String?> = emptyList()) : Parcelable
