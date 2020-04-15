package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(@SerializedName("place_id") val placeId: String = "",
                 @SerializedName("display_address") val displayAddress: String = "",
                 @SerializedName("type") val type: PoiType? = null) : Parcelable