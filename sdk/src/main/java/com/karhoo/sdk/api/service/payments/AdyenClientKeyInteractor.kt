package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.adyen.AdyenClientKey
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AdyenClientKeyInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                            private val apiTemplate: APITemplate,
                                                            private val context:
                                                            CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<AdyenClientKey>(true, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<AdyenClientKey>> {
        return GlobalScope.async {
            return@async getClientKey()
        }
    }

    private suspend fun getClientKey(): Resource<AdyenClientKey> {
        return when (val result = apiTemplate.getAdyenClientKey()
            .await()) {
            is Resource.Success -> Resource.Success(data = result.data)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }
}
