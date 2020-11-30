package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Direction(@SerializedName("kph") val kph: Int = 0,
                    @SerializedName("heading") val heading: Int = 0) : Parcelable
