package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentMethods(@SerializedName("groups") val groupAdyens: List<AdyenPaymentMethodsGroup>,
                               @SerializedName("paymentMethods") val adyenPaymentMethods: List<AdyenPaymentMethod>)
    : Parcelable