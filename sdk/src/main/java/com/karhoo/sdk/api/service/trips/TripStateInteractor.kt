package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BasePollCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class TripStateInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                       private val apiTemplate: APITemplate,
                                                       context: CoroutineContext = Main)
    : BasePollCallInteractor<TripState>(true, credentialsManager, apiTemplate, context) {

    internal var tripId: String? = null

    override fun createRequest(): Deferred<Resource<TripState>> {
        tripId?.let { tripId ->
            return apiTemplate.status(tripId)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}

