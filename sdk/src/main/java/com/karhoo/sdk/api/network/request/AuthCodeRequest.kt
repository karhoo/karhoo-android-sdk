package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthCodeRequest(@SerializedName("client_id") val clientId: String,
                           @SerializedName("scope") val scope: String,
                           @SerializedName("redirect_uri") val redirectUri: String,
                           @SerializedName("token") val token: String,
                           @SerializedName("code_challenge") val codeChallenge: String,
                           @SerializedName("code_challenge_method") val codeChallengeMethod: String) : Parcelable
