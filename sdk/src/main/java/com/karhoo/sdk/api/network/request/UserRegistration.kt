package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserRegistration(@SerializedName("first_name") val firstName: String?,
                            @SerializedName("last_name") val lastName: String?,
                            @SerializedName("email") val email: String?,
                            @SerializedName("password") val password: String?,
                            @SerializedName("phone_number") val phoneNumber: String?,
                            @SerializedName("locale") val locale: String = "") : Parcelable
