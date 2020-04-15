package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.TripStatus
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripSearch(@SerializedName("trip_states") private val tripState: List<TripStatus>? = null,
                      @SerializedName("trip_type") private val tripType: String? = null,
                      @SerializedName("pagination_offset") private val paginationOffset: Int = 0,
                      @SerializedName("pagination_row_count") private val paginationRowCount: Int = 0) : Parcelable