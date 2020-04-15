package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLogin(@SerializedName("username") val email: String,
                     @SerializedName("password") val password: String) : Parcelable
