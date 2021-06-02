package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.network.request.Passengers
import com.karhoo.sdk.api.network.request.Payer
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class TripInfo(@SerializedName("id") val tripId: String = "",
                    @SerializedName("passengers") val passengers: Passengers? = null,
                    @SerializedName("partner_traveller_id") val partnerTravellerId: String? = null,
                    @SerializedName("display_trip_id") val displayTripId: String = "",
                    @SerializedName("origin") val origin: TripLocationInfo? = null,
                    @SerializedName("destination") val destination: TripLocationInfo? = null,
                    @SerializedName("date_scheduled") val dateScheduled: Date? = null,
                    @SerializedName("status") val tripState: TripStatus? = null,
                    @SerializedName("state_details") val tripStateDetails: TripStatusDetails? = null,
                    @SerializedName("quote") val quote: Price? = null,
                    @SerializedName("external_trip_id") val externalId: String? = null,
                    @SerializedName("vehicle") val vehicle: Vehicle? = null,
                    @SerializedName("fleet_info") val fleetInfo: FleetInfo? = null,
                    @SerializedName("partner_trip_id") val partnerTripId: String? = null,
                    @SerializedName("flight_number") val flightNumber: String? = null,
                    @SerializedName("train_number") val trainNumber: String? = null,
                    @SerializedName("follow_code") var followCode: String? = null,
                    @SerializedName("comments") val comments: String? = null,
                    @SerializedName("meeting_point") val meetingPoint: MeetingPoint? = null,
                    @SerializedName("date_booked") val dateBooked: String? = null,
                    @SerializedName("agent") val agent: Agent? = null,
                    @SerializedName("cost_center_reference") val costReference: String? = null,
                    @SerializedName("cancelled_by") val cancelledBy: Payer? = null,
                    @SerializedName("meta") val meta: Map<String, String>? = null,
                    @SerializedName("train_time") val trainTime: String? = null,
                    @SerializedName("service_level_agreements") val serviceAgreements: ServiceAgreements? = null) : Parcelable
