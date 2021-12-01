package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyPreAuthPayload(@SerializedName("currency") val currency: String? = null,
                                 @SerializedName("points") val points: Int? = null,
                                 @SerializedName("flexpay") val flexpay: Boolean? = null,
                                 @SerializedName("loyalty_membership") val membership: String? = null) :    Parcelable
