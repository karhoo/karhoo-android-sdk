package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationInfoRequest(@SerializedName("place_id") val placeId: String,
                               @SerializedName("session_token") val sessionToken: String) : Parcelable

