package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookingFee(@SerializedName("cancellation_fee") val cancellationFee: Boolean = false,
                      @SerializedName("fee") val fee: BookingFeePrice? = BookingFeePrice()) : Parcelable
