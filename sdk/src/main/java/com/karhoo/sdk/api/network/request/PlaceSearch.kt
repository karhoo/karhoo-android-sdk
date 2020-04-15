package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.Position
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceSearch(val position: Position,
                       val query: String,
                       @SerializedName("session_token") val sessionToken: String) : Parcelable


