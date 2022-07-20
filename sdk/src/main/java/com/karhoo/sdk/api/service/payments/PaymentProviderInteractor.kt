package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.NonceRequest
import com.karhoo.sdk.api.network.request.Payer
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class PaymentProviderInteractor @Inject constructor(
    credentialsManager: CredentialsManager,
    private val userManager: UserManager,
    private val apiTemplate: APITemplate,
    private val paymentsService: PaymentsService,
    context: CoroutineContext
    = Dispatchers.Main
) : BaseCallInteractor<PaymentProvider>(true, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<PaymentProvider>> {
        return GlobalScope.async {
            return@async getPaymentProvider()
        }
    }

    private suspend fun getPaymentProvider(): Resource<PaymentProvider> {
        return when (val result = apiTemplate.getPaymentProvider().await()) {
            is Resource.Success -> {
                val paymentProvider = result.data
                userManager.paymentProvider = paymentProvider
                if (paymentProvider.provider.id.equals("Braintree", true) && userManager.user.organisations.isNotEmpty()) {
                    fetchUserCardDetails(user = userManager.user)
                }
                Resource.Success(data = result.data)
            }
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }

    private fun fetchUserCardDetails(user: UserInfo) {
        if (user.userId.isEmpty()) {
            return
        }

        val nonceRequest = NonceRequest(
            payer = Payer(
                id = user.userId,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName
            ),
            organisationId = user.organisations.first().id
        )
        paymentsService.getNonce(nonceRequest).execute { }
    }

}
