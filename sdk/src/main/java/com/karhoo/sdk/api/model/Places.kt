package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Places(@SerializedName("locations") val locations: List<Place> = emptyList()) : Parcelable
