package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentsNonce(@SerializedName("nonce") val nonce: String = "",
                         @SerializedName("card_type") val cardType: CardType?,
                         @SerializedName("last_four") val lastFour: String = "") : Parcelable
