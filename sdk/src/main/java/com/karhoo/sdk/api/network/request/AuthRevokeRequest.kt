package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthRevokeRequest(@SerializedName("token") val token: String,
                             @SerializedName("token_type_hint") val tokenTypeHint: String,
                             @SerializedName("client_id") val clientId: String) : Parcelable
