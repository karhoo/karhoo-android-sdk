package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RefreshTokenRequest(@SerializedName("refresh_token") private val token: String) : Parcelable
