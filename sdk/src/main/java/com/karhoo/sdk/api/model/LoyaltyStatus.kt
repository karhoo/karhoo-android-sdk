package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyStatus (@SerializedName("balance") var points: Int? = 0,
                          @SerializedName("can_burn") var burnable: Boolean? = false,
                          @SerializedName("can_earn") var earnable: Boolean? = false) : Parcelable
