package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AdyenPaymentsInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<JSONObject>(true, credentialsManager, apiTemplate, context) {

    var adyenPaymentsRequest: String? = null
    var version: String = ""

    override fun createRequest(): Deferred<Resource<JSONObject>> {
        adyenPaymentsRequest?.let {
            return GlobalScope.async {
                return@async getAdyenPayments(it)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun getAdyenPayments(adyenPaymentsRequest: String):
            Resource<JSONObject> {
        if(version.isNotBlank()){
            return when (val result = apiTemplate.getAdyenPayments(version, adyenPaymentsRequest).await()) {
                is Resource.Success -> {
                    val responseBody = result.data.string()
                    if (responseBody.isNullOrBlank()) {
                        Resource.Failure(error = KarhooError.InternalSDKError)
                    } else {
                        val response = JSONObject(responseBody)
                        Resource.Success(data = response)
                    }
                }
                is Resource.Failure -> Resource.Failure(error = result.error)
            }
        }else {
            return when (val result = apiTemplate.getAdyenPayments(adyenPaymentsRequest).await()) {
                is Resource.Success -> {
                    val responseBody = result.data.string()
                    if (responseBody.isNullOrBlank()) {
                        Resource.Failure(error = KarhooError.InternalSDKError)
                    } else {
                        val response = JSONObject(responseBody)
                        Resource.Success(data = response)
                    }
                }
                is Resource.Failure -> Resource.Failure(error = result.error)
            }
        }
    }

}
