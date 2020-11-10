package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoverageRequest(@SerializedName("latitude") val latitude: String,
                           @SerializedName("longitude") val longitude: String,
                           @SerializedName("local_time_of_pickup") val dateScheduled: String?) : Parcelable
