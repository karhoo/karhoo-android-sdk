package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentProvider(@SerializedName("id") val provider: String = "",
                           @SerializedName("loyalty_programmes") val loyalty: List<LoyaltyProgramme> =
                                   listOf()) :
        Parcelable