package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.karhoo.sdk.api.model.LoggingEvent
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoggingRequest(val events: List<LoggingEvent>): Parcelable
