package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

// Like iOS, parameters are unknown at this time
@Parcelize
data class AdyenSDKData(
        @SerializedName("MD") val md: String = "",
        @SerializedName("PaReq") val paReq: String = "",
        @SerializedName("TermUrl") val termUrl: String = "")
    : Parcelable