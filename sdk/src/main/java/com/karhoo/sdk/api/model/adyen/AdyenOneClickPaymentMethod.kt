package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenOneClickPaymentMethod(
        @SerializedName("details") val details: List<AdyenOneClickDetail>,
        @SerializedName("name") val name: String,
        @SerializedName("recurringDetailReference") val recurringDetailReference: String,
        @SerializedName("storedDetails") val storedDetails: AdyenStoredDetails,
        @SerializedName("type") val type: String)
    : Parcelable