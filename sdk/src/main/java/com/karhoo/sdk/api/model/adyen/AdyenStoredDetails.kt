package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenStoredDetails(
        @SerializedName("bank") val bank: AdyenBank,
        @SerializedName("card") val card: AdyenCard)
    : Parcelable