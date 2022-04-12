package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdyenClientKey(
    @SerializedName("clientKey") val clientKey: String = "",
    @SerializedName("environment") val environment: String = ""
) : Parcelable
