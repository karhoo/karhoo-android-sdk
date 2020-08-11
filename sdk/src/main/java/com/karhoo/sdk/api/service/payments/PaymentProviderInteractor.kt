package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class PaymentProviderInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                             private val apiTemplate: APITemplate,
                                                             context: CoroutineContext
                                                             = Dispatchers.Main)
    : BaseCallInteractor<PaymentProvider>(true, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<PaymentProvider>> {
        return GlobalScope.async {
            return@async getPaymentProvider()
        }
    }

    private suspend fun getPaymentProvider(): Resource<PaymentProvider> {
        val result = apiTemplate.getPaymentProvider().await()
        return when (result) {
            is Resource.Success -> Resource.Success(data = result.data)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }


}