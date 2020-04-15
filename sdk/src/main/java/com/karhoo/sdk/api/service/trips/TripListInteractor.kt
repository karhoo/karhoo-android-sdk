package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.TripSearch
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class TripListInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                      private val apiTemplate: APITemplate,
                                                      context: CoroutineContext = Main)
    : BaseCallInteractor<List<TripInfo>>(true, credentialsManager, apiTemplate, context) {

    var tripHistory: TripSearch? = null

    override fun createRequest(): Deferred<Resource<List<TripInfo>>> {
        tripHistory?.let { tripHistory ->
            return GlobalScope.async {
                getTripHistory(tripHistory)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun getTripHistory(tripHistory: TripSearch): Resource<List<TripInfo>> {
        val result = apiTemplate.tripHistory(tripHistory).await()
        return when (result) {
            is Resource.Success -> Resource.Success(data = result.data.bookings)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }
}
