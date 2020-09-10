package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenDetail(@SerializedName("items") val items: List<AdyenItem> = emptyList(),
                       @SerializedName("key") val key: String? = "",
                       @SerializedName("optional") val optional: Boolean,
                       @SerializedName("type") val type: String? = "",
                       val config: Map<String,String> = emptyMap())
    : Parcelable
