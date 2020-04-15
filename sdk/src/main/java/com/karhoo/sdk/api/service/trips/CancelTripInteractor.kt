package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.CancellationRequest
import com.karhoo.sdk.api.network.request.TripCancellation
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class CancelTripInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                        private val apiTemplate: APITemplate,
                                                        context: CoroutineContext = Main)
    : BaseCallInteractor<Void>(true, credentialsManager, apiTemplate, context) {

    var tripCancellation: TripCancellation? = null

    override fun createRequest(): Deferred<Resource<Void>> {
        tripCancellation?.let {
            return apiTemplate.cancel(it.tripId, CancellationRequest(reason = it.reason))
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
