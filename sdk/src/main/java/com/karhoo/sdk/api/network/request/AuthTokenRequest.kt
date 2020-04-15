package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthTokenRequest(@SerializedName("code") val code: String,
                            @SerializedName("redirect_uri") val redirectUri: String,
                            @SerializedName("code_verifier") val codeVerifier: String,
                            @SerializedName("grant_type") val grantType: String,
                            @SerializedName("client_id") val clientId: String) : Parcelable
