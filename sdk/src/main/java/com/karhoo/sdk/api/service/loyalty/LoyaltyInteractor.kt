package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class LoyaltyInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                    private val apiTemplate: APITemplate,
                                                    context: CoroutineContext = Main) :
        BaseCallInteractor<LoyaltyBalance>(true, credentialsManager, apiTemplate, context) {

            internal var loyaltyId: String? = null

    override fun createRequest(): Deferred<Resource<LoyaltyBalance>> {
        loyaltyId?.let { loyaltyId ->
            return apiTemplate.getLoyaltyBalance(loyaltyId)
        } ?: kotlin.run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }


}