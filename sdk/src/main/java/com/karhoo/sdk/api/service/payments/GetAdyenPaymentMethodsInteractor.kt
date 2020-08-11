package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.adyen.PaymentMethods
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class GetAdyenPaymentMethodsInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                                    private val apiTemplate: APITemplate,
                                                                    private val context:
                                                               CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<PaymentMethods>(true, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<PaymentMethods>> {
        return GlobalScope.async {
            getPaymentMethods()
        }
    }

    private suspend fun getPaymentMethods(): Resource<PaymentMethods> {
        return when (val result = apiTemplate.getPaymentMethods().await()) {
            is Resource.Success -> Resource.Success(data = result.data)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }

}