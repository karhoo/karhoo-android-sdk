package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyBalance(@SerializedName("points") var points: Int? = 0,
                          @SerializedName("can_burn") var burnable: Boolean? = false) : Parcelable