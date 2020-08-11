package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(@SerializedName("name") val name: String,
                 @SerializedName("types") val types: List<String>)
    : Parcelable