package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class AvailabilityRequest(@SerializedName("origin_place_id") private val originPlaceId: String,
                               @SerializedName("destination_place_id") private val destinationPlaceId: String,
                               @SerializedName("date_required") private val dateScheduled: Date?) : Parcelable
