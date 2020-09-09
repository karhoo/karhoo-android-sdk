package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsResponse
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AdyenPaymentsRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AdyenPaymentsInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<AdyenPaymentsResponse>(true, credentialsManager, apiTemplate, context) {

    var adyenPaymentsRequest: AdyenPaymentsRequest? = null

    override fun createRequest(): Deferred<Resource<AdyenPaymentsResponse>> {
        adyenPaymentsRequest?.let {
            return GlobalScope.async {
                return@async getAdyenPayments(it)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun getAdyenPayments(adyenPaymentsRequest: AdyenPaymentsRequest): Resource<AdyenPaymentsResponse> {
        return when (val result = apiTemplate.getAdyenPayments(adyenPaymentsRequest).await()) {
            is Resource.Success -> Resource.Success(data = result.data)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }

}
