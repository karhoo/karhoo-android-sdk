package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProviderID(@SerializedName("provider_name") val provider: String = "",
                      @SerializedName("loyalty_program") val loyalty: List<LoyaltyProgrammes> = listOf()) :
        Parcelable