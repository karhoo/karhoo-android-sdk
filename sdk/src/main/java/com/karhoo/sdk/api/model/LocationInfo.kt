package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationInfo(
        @SerializedName(value = "position", alternate = ["latlong"]) val position: Position? = null,
        @SerializedName("place_id") val placeId: String = "",
        @SerializedName("time_zone") val timezone: String = "",
        @SerializedName("poi_type") val poiType: Poi = Poi.NOT_SET,
        @SerializedName("details") val details: PoiDetails = PoiDetails(),
        @SerializedName("meeting_point") val meetingPoint: MeetingPoint = MeetingPoint(),
        @SerializedName("address") private val address: Address = Address())
    : Parcelable {

    val displayAddress: String
        get() = address.displayAddress
}
