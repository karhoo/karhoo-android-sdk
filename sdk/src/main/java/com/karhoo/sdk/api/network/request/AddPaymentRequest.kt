package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddPaymentRequest(@SerializedName("payer") val payer: Payer,
                             @SerializedName("organisation_id") val organisationId: String,
                             @SerializedName("nonce") val nonce: String) : Parcelable