package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotesRequest(@SerializedName("origin_place_id") val originPlaceId: String?,
                         @SerializedName("destination_place_id") val destinationPlaceId: String?,
                         @SerializedName("local_time_of_pickup") val dateScheduled: String?) : Parcelable