package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Provider(@SerializedName("id") val id: String = "",
                    @SerializedName("version") val version: String = "")
    : Parcelable
