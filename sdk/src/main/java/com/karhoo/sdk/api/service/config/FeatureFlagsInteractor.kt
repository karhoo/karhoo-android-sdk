package com.karhoo.sdk.api.service.config

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.FeatureFlag
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.FeatureFlagsRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class FeatureFlagsInteractor @Inject constructor(
    credentialsManager: CredentialsManager,
    private val apiTemplate: APITemplate,
    context: CoroutineContext = Dispatchers.Main
) : BaseCallInteractor<List<FeatureFlag>>(false, credentialsManager, apiTemplate, context) {

    lateinit var featureFlagsRequest: FeatureFlagsRequest

    override fun createRequest(): Deferred<Resource<List<FeatureFlag>>> {
        return GlobalScope.async {
            return@async getFeatureFlags(featureFlagsRequest.url)
        }
    }

    private suspend fun getFeatureFlags(url: String): Resource<List<FeatureFlag>> {
        return when (val result = apiTemplate.featureFlags(url).await()) {
            is Resource.Success -> Resource.Success(data = result.data)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }
}

