package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class TripInfo(@SerializedName("id") val tripId: String = "",
                    @SerializedName("display_trip_id") val displayTripId: String = "",
                    @SerializedName("origin") val origin: TripLocationInfo? = null,
                    @SerializedName("destination") val destination: TripLocationInfo? = null,
                    @SerializedName("date_scheduled") val dateScheduled: Date? = null,
                    @SerializedName("status") val tripState: TripStatus? = null,
                    @SerializedName("quote") val quote: Price? = null,
                    @SerializedName("vehicle") val vehicle: Vehicle? = null,
                    @SerializedName("fleet_info") val fleetInfo: FleetInfo? = null,
                    @SerializedName("flight_number") val flightNumber: String? = null,
                    @SerializedName("follow_code") var followCode: String? = null,
                    @SerializedName("comments") val comments: String? = null,
                    @SerializedName("meeting_point") val meetingPoint: MeetingPoint? = null)
    : Parcelable
