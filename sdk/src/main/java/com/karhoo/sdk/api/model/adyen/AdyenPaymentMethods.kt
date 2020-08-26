package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentMethods(@SerializedName("groups") val groupAdyens:
                               List<AdyenPaymentMethodsGroup> = emptyList(),
                               @SerializedName("paymentMethods") val adyenPaymentMethods:
                               List<AdyenPaymentMethod> = emptyList())
    : Parcelable