package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoyaltyNonce
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LoyaltyPreAuthPayload
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class LoyaltyPreAuthInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                                  private val apiTemplate: APITemplate,
                                                                  context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<LoyaltyNonce>(true, credentialsManager, apiTemplate, context) {

    var preAuthRequest: LoyaltyPreAuthPayload = LoyaltyPreAuthPayload()
    internal var loyaltyId: String? = null

    override fun createRequest(): Deferred<Resource<LoyaltyNonce>> {
        loyaltyId?.let { loyaltyId ->
            return apiTemplate.postLoyaltyPreAuth(loyaltyId, preAuthRequest)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }
}
