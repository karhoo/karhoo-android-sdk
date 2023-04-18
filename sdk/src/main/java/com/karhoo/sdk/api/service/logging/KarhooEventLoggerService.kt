package com.karhoo.sdk.api.service.logging

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoggingResponse
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LoggingRequest
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooEventLoggerService : EventLoggerService {
    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun sendLogs(loggingRequest: LoggingRequest): Call<LoggingResponse> =
        EventLoggerInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate
        ).apply {
            this.loggingRequest = loggingRequest
        }
}
