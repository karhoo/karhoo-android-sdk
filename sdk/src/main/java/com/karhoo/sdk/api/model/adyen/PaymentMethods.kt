package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethods(@SerializedName("groups") val groups: List<Group>,
                          @SerializedName("paymentMethods") val paymentMethods: List<PaymentMethod>)
    : Parcelable