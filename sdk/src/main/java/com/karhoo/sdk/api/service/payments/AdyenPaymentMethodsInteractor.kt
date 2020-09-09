package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AdyenPaymentMethodsRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AdyenPaymentMethodsInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                                 private val apiTemplate: APITemplate,
                                                                 private val context:
                                                                 CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<String>(true, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<String>> {
        return GlobalScope.async {
            return@async getPaymentMethods()
        }
    }

    private suspend fun getPaymentMethods(): Resource<String> {
        return when (val result = apiTemplate.getAdyenPaymentMethods(AdyenPaymentMethodsRequest())
                .await()) {
            is Resource.Success -> {
                val responseBody = result.data.string()
                if (responseBody.isNullOrBlank()) {
                    Resource.Failure(error = KarhooError.InternalSDKError)
                } else {
                    Resource.Success(data = responseBody)
                }
            }
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }

}