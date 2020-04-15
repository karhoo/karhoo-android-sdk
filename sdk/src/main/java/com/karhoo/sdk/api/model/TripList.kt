package com.karhoo.sdk.api.model

import android.os.Parcelable

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripList(@SerializedName("bookings") val bookings: List<TripInfo> = emptyList()) : Parcelable
