package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class ReverseGeolocateInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                              private val apiTemplate: APITemplate,
                                                              context: CoroutineContext = Main)
    : BaseCallInteractor<LocationInfo>(true, credentialsManager, apiTemplate, context) {

    internal var position: Position? = null

    override fun createRequest(): Deferred<Resource<LocationInfo>> {
        position?.let { position ->
            return apiTemplate.reverseGeocode(latitude = position.latitude, longitude = position
                    .longitude)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
