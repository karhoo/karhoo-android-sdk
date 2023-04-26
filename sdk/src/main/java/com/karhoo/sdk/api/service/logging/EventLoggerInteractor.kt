package com.karhoo.sdk.api.service.logging

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoggingResponse
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LoggingRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

internal class EventLoggerInteractor(
    credentialsManager: CredentialsManager,
    private val apiTemplate: APITemplate,
    context: CoroutineContext = Dispatchers.Main
) : BaseCallInteractor<LoggingResponse>(true, credentialsManager, apiTemplate, context) {

    internal var loggingRequest: LoggingRequest? = null

    override fun createRequest(): Deferred<Resource<LoggingResponse>> {
        return GlobalScope.async {
            return@async sendLogs()
        }
    }

    private suspend fun sendLogs(): Resource<LoggingResponse> {
        loggingRequest?.let { request ->
            return when (val result = apiTemplate.sendLogs(URL, request.events).await()) {
                is Resource.Success -> Resource.Success(data = result.data)
                is Resource.Failure -> Resource.Failure(error = result.error)
            }
        } ?: run {
            return Resource.Failure(error = KarhooError.InternalSDKError)
        }
    }

    companion object {
        const val URL = "karhoo.com/logs"
    }
}
