package com.karhoo.sdk.api.network.request

import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.CancellationReason

//TODO: Is this meant to be without a parcelize

data class CancellationRequest(@SerializedName("reason") val reason: CancellationReason = CancellationReason.OTHER_USER_REASON,
                              @SerializedName("explanation") val explanation: String? = "")
