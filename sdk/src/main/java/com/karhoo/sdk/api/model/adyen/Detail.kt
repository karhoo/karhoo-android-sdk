package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Detail(@SerializedName("items") val items: List<Item>,
                  @SerializedName("key") val key: String,
                  @SerializedName("optional") val optional: Boolean,
                  @SerializedName("type") val type: String,
                  val config: Map<String,String>)
    : Parcelable