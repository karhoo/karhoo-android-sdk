package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyProgramme(@SerializedName("id") val loyaltyID: String = "",
                            @SerializedName("name") val loyaltyName: String = "") : Parcelable
