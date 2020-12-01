package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.CancellationReason
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CancellationRequest(@SerializedName("reason") val reason: CancellationReason = CancellationReason.OTHER_USER_REASON,
                              @SerializedName("explanation") val explanation: String? = "") : Parcelable
