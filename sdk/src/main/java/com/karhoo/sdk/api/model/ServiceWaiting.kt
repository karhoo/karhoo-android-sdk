package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ServiceWaiting(@SerializedName("minutes") val minutes: Int = 0) : Parcelable
