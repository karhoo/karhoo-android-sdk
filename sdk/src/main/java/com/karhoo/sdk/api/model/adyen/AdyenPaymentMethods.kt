package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentMethods(@SerializedName("groups") val groups:
                               List<AdyenPaymentMethodsGroup> = emptyList(),
                               @SerializedName("oneClickPaymentMethods") val oneClickPaymentMethods:
                               List<AdyenOneClickPaymentMethod> = emptyList(),
                               @SerializedName("paymentMethods") val paymentMethods:
                               List<AdyenPaymentMethod> = emptyList(),
                               @SerializedName("storedPaymentMethods") val storedPaymentMethods:
                               List<AdyenStoredPaymentMethod> = emptyList())
    : Parcelable