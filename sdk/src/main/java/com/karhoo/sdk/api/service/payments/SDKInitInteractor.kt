package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class SDKInitInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                     private val apiTemplate: APITemplate,
                                                     context: CoroutineContext = Main)
    : BaseCallInteractor<BraintreeSDKToken>(true, credentialsManager, apiTemplate, context) {

    var sdkInitRequest: SDKInitRequest? = null

    override fun createRequest(): Deferred<Resource<BraintreeSDKToken>> {
        sdkInitRequest?.let {
            return apiTemplate.sdkInitToken(it.organisationId, it.currency)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }
}
