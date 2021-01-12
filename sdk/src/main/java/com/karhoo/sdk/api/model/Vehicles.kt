package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vehicles(@SerializedName("id") val id: String = "",
                    @SerializedName("availability") val availability: Availability = Availability(),
                    @SerializedName("quotes") val quotes: List<Quote> = emptyList(),
                    @SerializedName("status") val status: String? = "",
                    @SerializedName("validity") val validity: Int = 0) : Parcelable
