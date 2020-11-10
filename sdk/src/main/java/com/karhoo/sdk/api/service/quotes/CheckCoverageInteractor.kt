package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Coverage
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.CoverageRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class CheckCoverageInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           context: CoroutineContext = Main) :
        BaseCallInteractor<Coverage>(true, credentialsManager, apiTemplate, context) {

    internal var coverageRequest: CoverageRequest? = null

    override fun createRequest(): Deferred<Resource<Coverage>> {
        return GlobalScope.async {
            return@async checkCoverage()
        }
    }

    private suspend fun checkCoverage(): Resource<Coverage> {
        coverageRequest?.let { request ->
            return when (val result = apiTemplate.checkCoverage(latitude = request.latitude,
                                                                longitude = request.longitude, dateScheduled =
                                                                request.dateScheduled)
                    .await()) {
                is Resource.Success -> Resource.Success(data = result.data)
                is Resource.Failure -> Resource.Failure(error = result.error)
            }
        } ?: run {
            return Resource.Failure(error = KarhooError.InternalSDKError)
        }
    }
}
