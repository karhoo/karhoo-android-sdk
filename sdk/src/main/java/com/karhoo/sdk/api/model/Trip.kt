package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trip(val id: String? = null,
                val supplierId: String? = null,
                val origin: LocationInfo? = null,
                val destination: LocationInfo? = null,
                val price: Float = 0f,
                val currency: String? = null) : Parcelable
