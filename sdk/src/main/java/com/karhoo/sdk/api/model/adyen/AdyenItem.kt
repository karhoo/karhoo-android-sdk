package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenItem(@SerializedName("id") val id: String? = "",
                     @SerializedName("name") val name: String? = "")
    : Parcelable