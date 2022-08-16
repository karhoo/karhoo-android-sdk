package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VehicleMappings(
    @Expose
    @SerializedName("mappings") val mappings: List<VehicleMapping>? = null,
    @Expose
    @SerializedName("badgeMappings") val badgeMappings: List<VehicleMapping>? = null
) : Parcelable
