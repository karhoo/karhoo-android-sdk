package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class QuotesSearch(val origin: LocationInfo,
                        val destination: LocationInfo,
                        val dateScheduled: Date? = null) : Parcelable