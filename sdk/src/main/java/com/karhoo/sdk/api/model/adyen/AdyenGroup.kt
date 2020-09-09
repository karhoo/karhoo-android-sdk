package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenGroup(@SerializedName("name") val name: String? = "",
                      @SerializedName("paymentMethodData") val paymentMethodData: String? = "",
                      @SerializedName("types") val types: List<String> = emptyList())
    : Parcelable