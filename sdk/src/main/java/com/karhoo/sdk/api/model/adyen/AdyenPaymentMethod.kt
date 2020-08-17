package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentMethod(@SerializedName("brands") val brands: List<String>,
                              @SerializedName("details") val adyenDetails: List<AdyenDetail>,
                              @SerializedName("name") val name: String? = "",
                              @SerializedName("supportsRecurring") val supportsRecurring: Boolean,
                              @SerializedName("type") val type: String? = "")
    : Parcelable