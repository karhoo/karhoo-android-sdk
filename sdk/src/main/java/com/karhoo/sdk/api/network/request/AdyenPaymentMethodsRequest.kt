package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentMethodsRequest(@SerializedName("channel") val string: String = "Android",
                                      @SerializedName("merchantAccount") val merchantAccount:
                                         String = "")
    : Parcelable