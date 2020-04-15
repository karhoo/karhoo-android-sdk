package com.karhoo.sdk.api.model

import android.os.Parcelable

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vehicles(@SerializedName("id") val id: String? = null,
                    @SerializedName("status") val status: String? = null,
                    @SerializedName("category_names") val categoryNames: List<String> = emptyList(),
                    @SerializedName("quote_items") val vehicles: List<Quote> = emptyList()) : Parcelable