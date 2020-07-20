package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteRequestPoint(@SerializedName("latitude") val latitude: String,
                           @SerializedName("longitude") val longitude: String,
                           @SerializedName("display_address") val displayAddress: String?) : Parcelable