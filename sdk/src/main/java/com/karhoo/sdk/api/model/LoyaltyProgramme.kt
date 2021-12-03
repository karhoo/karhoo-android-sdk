package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoyaltyProgramme(@SerializedName("id") val id: String? = null,
                            @SerializedName("name") val name: String? = null) : Parcelable
