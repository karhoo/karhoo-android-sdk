package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyPreAuth(@SerializedName("currency") val currency: String? = null,
                       @SerializedName("points") val points: Int? = null,
                       @SerializedName("flexpay") val flexpay: Boolean? = null,
                       @SerializedName("loyalty_membership") val loyaltyMembership: String? = null) : Parcelable
