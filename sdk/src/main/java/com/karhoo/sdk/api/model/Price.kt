package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Price(@SerializedName("total") val total: Int = 0,
                 @SerializedName("currency") val currency: String? = null,
                 @SerializedName("type") val quoteType: QuoteType = QuoteType.FIXED) : Parcelable
