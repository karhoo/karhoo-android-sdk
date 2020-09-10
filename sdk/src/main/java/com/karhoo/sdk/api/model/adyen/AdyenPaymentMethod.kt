package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentMethod(@SerializedName("brands") val brands: List<String> = emptyList(),
                              @SerializedName("details") val details: List<AdyenDetail> = emptyList(),
                              @SerializedName("group") val group: AdyenGroup? = AdyenGroup(),
                              @SerializedName("name") val name: String? = "",
                              @SerializedName("supportsRecurring") val supportsRecurring: Boolean
                              = false,
                              @SerializedName("type") val type: String? = "")
    : Parcelable
