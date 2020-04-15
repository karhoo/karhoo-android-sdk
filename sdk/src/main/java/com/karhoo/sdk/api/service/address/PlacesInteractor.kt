package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Places
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.PlaceSearch
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class PlacesInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                    private val apiTemplate: APITemplate,
                                                    context: CoroutineContext = Main)
    : BaseCallInteractor<Places>(true, credentialsManager, apiTemplate, context) {

    internal var placeSearch: PlaceSearch? = null

    override fun createRequest(): Deferred<Resource<Places>> {
        placeSearch?.let { placeSearch ->
            return apiTemplate.placeSearch(placeSearch)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
