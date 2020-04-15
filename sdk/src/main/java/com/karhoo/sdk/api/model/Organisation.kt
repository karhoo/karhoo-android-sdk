package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Organisation(@SerializedName("id") val id: String,
                        @SerializedName("name") val name: String,
                        @SerializedName("roles") val roles: List<String>?)
    : Parcelable