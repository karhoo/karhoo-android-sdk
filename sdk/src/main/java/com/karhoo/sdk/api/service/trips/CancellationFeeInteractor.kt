package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.BookingFee
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class CancellationFeeInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                      private val apiTemplate: APITemplate,
                                                      context: CoroutineContext = Main)
    : BaseCallInteractor<BookingFee>(true, credentialsManager, apiTemplate, context) {

    internal var feeIdentifier: String? = null

    override fun createRequest(): Deferred<Resource<BookingFee>> {
        feeIdentifier?.let { feeIdentifier ->
            return apiTemplate.cancellationFee(feeIdentifier)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))        }
    }
}