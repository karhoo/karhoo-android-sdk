package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Agent(@SerializedName("user_id") val userId: String? = "",
                @SerializedName("user_name") val userName: String? = "",
                @SerializedName("organisation_id") val organisationId: String? = "",
                @SerializedName("organisation_name") val organisationName: String? = ""): Parcelable
