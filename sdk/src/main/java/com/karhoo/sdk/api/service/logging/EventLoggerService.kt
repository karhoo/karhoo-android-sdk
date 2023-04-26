package com.karhoo.sdk.api.service.logging

import com.karhoo.sdk.api.model.LoggingResponse
import com.karhoo.sdk.api.network.request.LoggingRequest
import com.karhoo.sdk.call.Call

interface EventLoggerService {
    fun sendLogs(loggingRequest: LoggingRequest): Call<LoggingResponse>
}
