package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Driver(@SerializedName("first_name") val firstName: String = "",
                  @SerializedName("last_name") val lastName: String = "",
                  @SerializedName("phone_number") val phoneNumber: String? = "",
                  @SerializedName("photo_url") val photoUrl: String? = null,
                  @SerializedName("license_number") val licenceNumber: String = "") : Parcelable