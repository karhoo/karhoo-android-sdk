package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BasePollCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class MonitorTripInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                         private val apiTemplate: APITemplate,
                                                         context: CoroutineContext = Main)
    : BasePollCallInteractor<TripInfo>(true, credentialsManager, apiTemplate, context) {

    internal var tripIdentifier: String? = null

    override fun createRequest(): Deferred<Resource<TripInfo>> {
        tripIdentifier?.let { tripIdentifier ->
            return if (KarhooSDKConfigurationProvider.configuration.authenticationMethod() is
                            AuthenticationMethod.Guest) {
                apiTemplate.guestTripDetails(tripIdentifier)
            } else {
                apiTemplate.tripDetails(tripIdentifier)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }
}
