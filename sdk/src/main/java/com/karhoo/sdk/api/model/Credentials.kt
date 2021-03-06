package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Credentials(@SerializedName("access_token") val accessToken: String = "",
                       @SerializedName("refresh_token") val refreshToken: String = "",
                       @SerializedName("expires_in") val expiresIn: Long? = null,
                       @SerializedName("refresh_expires_in") val refreshExpiresIn: Long? = null) : Parcelable
