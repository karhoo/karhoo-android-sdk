package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class VerifyQuoteInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                         private val apiTemplate: APITemplate,
                                                         context: CoroutineContext = Main) :
        BaseCallInteractor<Quote>(true, credentialsManager, apiTemplate, context) {

    internal var quoteIdRequest: QuoteId? = null

    override fun createRequest(): Deferred<Resource<Quote>> {
        return GlobalScope.async {
            return@async verifyQuoteId()
        }
    }

    private suspend fun verifyQuoteId(): Resource<Quote> {
        quoteIdRequest?.let { request ->
            return when (val result = apiTemplate.verifyQuotes(id = request.quoteId).await()) {
                is Resource.Success -> Resource.Success(data = result.data)
                is Resource.Failure -> Resource.Failure(error = result.error)
            }
        } ?: kotlin.run {
            return Resource.Failure(error = KarhooError.InternalSDKError)
        }
    }
}