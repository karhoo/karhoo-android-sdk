package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.TripStatus
import com.karhoo.sdk.api.model.TripType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripSearch(@SerializedName("trip_states") private val tripState: List<TripStatus>? = null,
                      @SerializedName("trip_type") private val tripType: TripType? = null,
                      @SerializedName("created_after") private val createdAfter: String? = null,
                      @SerializedName("email") private val email: String? = null,
                      @SerializedName("partner_traveller_id") private val partnerId: String? = null,
                      @SerializedName("prebook_time_after") private val prebookTimeAfter: String? = null,
                      @SerializedName("prebook_time_before") private val prebookTimeBefore: String? = null,
                      @SerializedName("trip_time_before") private val tripTimeBefore: String? = null,
                      @SerializedName("trip_time_after") private val tripTimeAfter: String? = null,
                      @SerializedName("only_without_final_price") private val withoutFinalPrice: Boolean? = false,
                      @SerializedName("pagination_offset") private val paginationOffset: Int = 0,
                      @SerializedName("pagination_row_count") private val paginationRowCount: Int = 0,
                      @SerializedName("fleet_id") private val fleetId: String? = "",
                      @SerializedName("partner_trip_id") private val partnerTripId: String? = null,
                      @SerializedName("external_trip_id") private val externalId: String? = null,
                      @SerializedName("forename") private val forename: String? = null,
                      @SerializedName("lastname") private val lastname: String? = null,
                      @SerializedName("display_trip_id") private val displayId: String? = "",
                      @SerializedName("order_by") private val orderBy: List<String?> = emptyList()) : Parcelable
