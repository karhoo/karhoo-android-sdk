package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LocationInfoRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class LocationInfoInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                          private val apiTemplate: APITemplate,
                                                          context: CoroutineContext = Main)
    : BaseCallInteractor<LocationInfo>(true, credentialsManager, apiTemplate, context) {

    internal var locationInfoRequest: LocationInfoRequest? = null

    override fun createRequest(): Deferred<Resource<LocationInfo>> {
        locationInfoRequest?.let { locationInfoRequest ->
            return apiTemplate.locationInfo(locationInfoRequest)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
