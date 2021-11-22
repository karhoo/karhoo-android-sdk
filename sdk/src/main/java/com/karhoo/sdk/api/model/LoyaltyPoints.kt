package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyPoints(@SerializedName("points") var points: Int? = 0) : Parcelable
