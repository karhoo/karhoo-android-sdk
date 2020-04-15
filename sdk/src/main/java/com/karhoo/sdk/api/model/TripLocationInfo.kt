package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripLocationInfo(@SerializedName("display_address") val displayAddress: String = "",
                            @SerializedName(value = "position", alternate = arrayOf("latlong")) val position: Position? = null,
                            @SerializedName("place_id") val placeId: String = "",
                            @SerializedName("poi_type") val poiType: Poi = Poi.NOT_SET,
                            @SerializedName("timezone") val timezone: String = "")
    : Parcelable