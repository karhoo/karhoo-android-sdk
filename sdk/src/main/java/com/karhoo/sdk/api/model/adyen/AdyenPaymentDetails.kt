package com.karhoo.sdk.api.model.adyen

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdyenPaymentDetails(@SerializedName("MD") val MD: String = "",
                              @SerializedName("PaReq") val PaReq: String = "",
                              @SerializedName("PaRes") val PaRes: String = "",
                              @SerializedName("billingToken") val billingToken: String = "",
                              @SerializedName("cupSecurePlusSmsCode") val cupSecurePlusSmsCode: String = "cupsecureplus.smscode",
                              @SerializedName("facilitatorAccessToken") val facilitatorAccessToken: String = "",
                              @SerializedName("oneTimePasscode") val oneTimePasscode: String = "",
                              @SerializedName("orderID") val orderID: String = "",
                              @SerializedName("payerID") val payerID: String = "",
                              @SerializedName("payload") val payload: String = "",
                              @SerializedName("paymentID") val paymentID: String = "") :
        Parcelable