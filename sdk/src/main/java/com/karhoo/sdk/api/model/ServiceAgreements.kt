package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceAgreements(@SerializedName("free_cancellation") val freeCancellation: ServiceCancellation? = null,
                            @SerializedName("free_waiting_time") val freeWaitingTime: ServiceWaiting? = null) : Parcelable
