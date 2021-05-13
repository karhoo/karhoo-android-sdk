package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceCancellation(@SerializedName("type") val type: String = "",
                              @SerializedName("minutes") val minutes: Int = 0) : Parcelable
