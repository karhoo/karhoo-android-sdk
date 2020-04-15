package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class BookTripInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                      private val apiTemplate: APITemplate,
                                                      context: CoroutineContext = Main)
    : BaseCallInteractor<TripInfo>(true, credentialsManager, apiTemplate, context) {

    internal var tripBooking: TripBooking? = null

    override fun createRequest(): Deferred<Resource<TripInfo>> {
        tripBooking?.let {
            return apiTemplate.book(it)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
