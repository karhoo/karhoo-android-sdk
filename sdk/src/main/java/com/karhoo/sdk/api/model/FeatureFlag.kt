package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeatureFlag(
    @SerializedName("version") val version: String,
    @SerializedName("flags") var flags: Map<String, Boolean>
) : Parcelable
