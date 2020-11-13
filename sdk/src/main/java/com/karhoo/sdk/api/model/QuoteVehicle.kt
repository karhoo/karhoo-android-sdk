package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.network.request.QuoteQTA
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteVehicle(@SerializedName("type") val vehicleType: String? = "",
                        @SerializedName("class") val vehicleClass: String? = "",
                        @SerializedName("tags") val vehicleTags: List<String> = listOf(),
                        @SerializedName("qta") val vehicleQta: QuoteQTA = QuoteQTA()) : Parcelable
