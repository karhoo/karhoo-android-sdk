package com.karhoo.sdk.api.service.fare

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class FareInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                  private val apiTemplate: APITemplate,
                                                  context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<Fare>(true, credentialsManager, apiTemplate, context) {

    var tripId: String? = null

    override fun createRequest(): Deferred<Resource<Fare>> {
        tripId?.let {
            return apiTemplate.fareDetails(it)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }
}

