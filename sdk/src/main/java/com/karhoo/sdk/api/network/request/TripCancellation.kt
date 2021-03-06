package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.karhoo.sdk.api.model.CancellationReason
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripCancellation(val tripIdentifier: String,
                            val reason: CancellationReason = CancellationReason.OTHER_USER_REASON,
                            val explanation: String? = "") : Parcelable

