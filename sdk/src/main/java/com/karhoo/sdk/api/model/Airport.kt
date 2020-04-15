package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Airport(@SerializedName("code") val code: String?,
                   @SerializedName("name") val name: String?,
                   @SerializedName("flight_number") val flightNumber: String?,
                   @SerializedName("terminal") val terminal: String?) : Parcelable