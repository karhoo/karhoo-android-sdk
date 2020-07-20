package com.karhoo.sdk.api.service.availability

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AvailabilityRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@Deprecated("Availabilities endpoint is deprecated")
internal class AvailabilityInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                          private val apiTemplate: APITemplate,
                                                          val context: CoroutineContext = Main)
    : BaseCallInteractor<Categories>(true, credentialsManager, apiTemplate, context) {

    internal var quotesSearch: QuotesSearch? = null

    override fun createRequest(): Deferred<Resource<Categories>> {
        quotesSearch?.let { availabilitySearch ->
            val origin = availabilitySearch.origin.placeId
            val destination = availabilitySearch.destination.placeId
            val dateScheduled = availabilitySearch.dateScheduled

            val request = AvailabilityRequest(origin, destination, dateScheduled)
            return apiTemplate.availabilities(request)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }
}
