package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentProvider(@SerializedName("provider") val provider: Provider = Provider())
    : Parcelable