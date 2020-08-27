package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenData(
        @SerializedName("MD") val MD: String = "",
        @SerializedName("PaReq") val PaReq: String = "",
        @SerializedName("TermUrl") val TermUrl: String = "")
    : Parcelable