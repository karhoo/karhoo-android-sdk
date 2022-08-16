package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VehicleMapping(
    @Expose
    @SerializedName("type") val vehicleType: String? = null,
    @Expose
    @SerializedName("tags") val vehicleTags: List<String>? = null,
    @Expose
    @SerializedName("image_png") val vehicleImagePNG: String? = null,
    @Expose
    @SerializedName("image_svg") val vehicleImageSVG: String? = null
) : Parcelable
