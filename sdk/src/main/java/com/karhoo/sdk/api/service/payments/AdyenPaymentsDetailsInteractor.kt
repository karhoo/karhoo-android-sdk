package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AdyenPaymentsDetailsInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<String>(true, credentialsManager, apiTemplate, context) {

    var adyenPaymentsDetails: String? = null

    override fun createRequest(): Deferred<Resource<String>> {
        adyenPaymentsDetails?.let {
            return GlobalScope.async {
                return@async getAdyenPaymentsDetails(it)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun getAdyenPaymentsDetails(adyenPaymentsDetails: String):
            Resource<String> {
        return when (val result = apiTemplate.getAdyenPaymentDetails(adyenPaymentsDetails).await()) {
            is Resource.Success -> {
                val responseBody = result.data.string()
                if(responseBody.isNullOrBlank()) {
                    Resource.Failure(error = KarhooError.InternalSDKError)
                } else {
                    Resource.Success(data = responseBody)
                }
            }
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }

}