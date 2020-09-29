package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vehicles(@SerializedName("id") val id: String = "",
                    @SerializedName("status") val status: String? = null,
                    @SerializedName("availability") val availability: Availability = Availability(),
                    @SerializedName("quotes") val quotes: List<Quote> = emptyList()) : Parcelable
