package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AvailabilityVehicle(@SerializedName("classes") val classes: List<String> = listOf(),
                               @SerializedName("tags") val tags: List<String> = listOf(),
                               @SerializedName("types") val types: List<String> = listOf()) :
        Parcelable
