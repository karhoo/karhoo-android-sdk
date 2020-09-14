package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentDetail(@SerializedName("key") val key: String = "",
                              @SerializedName("type") val type: String = "") : Parcelable