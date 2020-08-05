package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyProgrammes(@SerializedName("loyalty_programmes_id") val loyaltyID: String = "",
                             @SerializedName("loyalty_programmes_name") val loyaltyName: String =
                                     "")
                             : Parcelable
