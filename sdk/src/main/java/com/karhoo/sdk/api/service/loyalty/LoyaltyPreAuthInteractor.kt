package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoyaltyNonce
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LoyaltyPreAuth
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

    var loyaltyPreAuth: LoyaltyPreAuth? = null

    override fun createRequest(): Deferred<Resource<LoyaltyNonce>> {
        loyaltyPreAuth?.let {
            return apiTemplate.postLoyaltyPreAuth(it)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }
}
