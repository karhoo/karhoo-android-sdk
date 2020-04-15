package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlightDetails(val flightNumber: String?,
                         val comments: String? = null) : Parcelable