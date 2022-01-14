package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoyaltyPoints
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class LoyaltyBurnPointsInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           context: CoroutineContext = Dispatchers.Main
                                                          ) :
    BaseCallInteractor<LoyaltyPoints>(true, credentialsManager, apiTemplate, context) {

    internal var loyaltyId: String? = null
    internal var currency: String = ""
    internal var amount: Int = 0

    override fun createRequest(): Deferred<Resource<LoyaltyPoints>> {
        loyaltyId?.let { loyaltyId ->
            return apiTemplate.loyaltyBurnPoints(loyaltyId, currency, amount)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
