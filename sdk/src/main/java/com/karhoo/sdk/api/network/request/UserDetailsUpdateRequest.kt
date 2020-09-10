package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetailsUpdateRequest(@Transient val userId: String,
                                    @SerializedName("phone_number") val phoneNumber: String,
                                    @SerializedName("first_name") val firstName: String,
                                    @SerializedName("last_name") val lastName: String,
                                    @SerializedName("locale") val locale: String,
                                    @SerializedName("avatar_url") val avatarUrl: String? = null) : Parcelable
