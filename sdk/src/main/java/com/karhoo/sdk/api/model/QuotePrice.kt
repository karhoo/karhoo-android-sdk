package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotePrice(@SerializedName("currency_code") val currencyCode: String? = null,
                      @SerializedName("high") val highPrice: Int = 0,
                      @SerializedName("low") val lowPrice: Int = 0,
                      @SerializedName("net") val net: QuotePriceNet = QuotePriceNet()) : Parcelable
