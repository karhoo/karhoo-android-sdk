package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.adyen.AdyenPublicKey
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
@Deprecated("Use the client-key instead")
internal class AdyenPublicKeyInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           private val context:
                                                           CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<AdyenPublicKey>(true, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<AdyenPublicKey>> {
        return GlobalScope.async {
            return@async getPublicKey()
        }
    }
    @Deprecated("Use the client-key instead")
    private suspend fun getPublicKey(): Resource<AdyenPublicKey> {
        return when (val result = apiTemplate.getAdyenPublicKey()
                .await()) {
            is Resource.Success -> Resource.Success(data = result.data)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }
}
