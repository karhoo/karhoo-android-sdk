package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.logging.Level

@Parcelize
data class LoggingEvent(val tag: String,
                   val message: String,
                   val severity: Level,
                   val date: String = Date().toString(),
                   val userId: String? = null,
                   val device: String?,
                   val platform: String?,
                   val version: String?): Parcelable
