package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenData(
        @SerializedName("MD") val md: String = "",
        @SerializedName("PaReq") val paReq: String = "",
        @SerializedName("TermUrl") val termUrl: String = "")
    : Parcelable
