package com.karhoo.sdk.api.service.payments

import com.google.gson.Gson
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsResponse
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AdyenPaymentsInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<String>(true, credentialsManager, apiTemplate, context) {

    var adyenPaymentsRequest: String? = null

    override fun createRequest(): Deferred<Resource<String>> {
        adyenPaymentsRequest?.let {
            return GlobalScope.async {
                return@async getAdyenPayments(it)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun getAdyenPayments(adyenPaymentsRequest: String):
            Resource<String> {
        return when (val result = apiTemplate.getAdyenPayments(adyenPaymentsRequest).await()) {
            is Resource.Success -> Resource.Success(data = result.data.toString())
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }

}
