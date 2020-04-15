package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.model.Vehicles
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AvailabilityRequest
import com.karhoo.sdk.api.network.request.QuotesRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BasePollCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.TimeZone
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class QuotesInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                    private val apiTemplate: APITemplate,
                                                    context: CoroutineContext = Main)
    : BasePollCallInteractor<QuoteList>(true, credentialsManager, apiTemplate, context) {

    internal var quotesSearch: QuotesSearch? = null
    private var categories: List<String> = mutableListOf()
    private var vehicles: Vehicles? = null
    private var quoteId: QuoteId? = null

    override fun createRequest(): Deferred<Resource<QuoteList>> {
        quotesSearch?.let { search ->
            quoteId?.let { quote ->
                return GlobalScope.async {
                    val result = quoteList(Resource.Success(Categories(categoryNames = categories)), quotes(quote).await())
                    if (result is Resource.Failure && result.error == KarhooError.CouldNotGetEstimates) {
                        quoteId = null
                        createRequest().await()
                    } else {
                        result
                    }
                }
            } ?: run {
                return GlobalScope.async {
                    quoteList(
                            categoriesResource = availability(createAvailabilityRequest(search)).await(),
                            quotesResource = quotes(createQuoteRequest(search)).await())
                }
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private fun createAvailabilityRequest(quotesSearch: QuotesSearch): AvailabilityRequest {
        return AvailabilityRequest(
                originPlaceId = quotesSearch.origin.placeId,
                destinationPlaceId = quotesSearch.destination.placeId,
                dateScheduled = quotesSearch.dateScheduled)
    }

    private fun createQuoteRequest(quotesSearch: QuotesSearch): QuotesRequest {
        val dateScheduled: String? = quotesSearch.dateScheduled?.let {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm").apply {
                timeZone = TimeZone.getTimeZone(quotesSearch.origin.timezone)
            }.format(it)
        }

        return QuotesRequest(
                originPlaceId = quotesSearch.origin.placeId,
                destinationPlaceId = quotesSearch.destination.placeId,
                dateScheduled = dateScheduled)
    }

    private fun quoteList(categoriesResource: Resource<Categories>, quotesResource: Resource<Vehicles>): Resource<QuoteList> {
        when (categoriesResource) {
            is Resource.Success -> this.categories = categoriesResource.data.categoryNames.orEmpty()
            is Resource.Failure -> return Resource.Failure(categoriesResource.error)
        }

        when (quotesResource) {
            is Resource.Success -> this.vehicles = quotesResource.data
            is Resource.Failure -> return Resource.Failure(quotesResource.error)
        }

        categories.let {
            val vehicles = Vehicles(
                    categoryNames = it,
                    vehicles = vehicles?.vehicles ?: emptyList())

            val quotesMap = mutableMapOf<String, List<Quote>>()
            it.forEach { category ->
                val filteredVehicles = vehicles.vehicles.filter { it.categoryName == category }
                quotesMap[category] = filteredVehicles
            }
            return Resource.Success(QuoteList(id = quoteId ?: QuoteId(), categories = quotesMap))
        }
    }

    private fun availability(request: AvailabilityRequest): Deferred<Resource<Categories>> {
        return apiTemplate.availabilities(request)
    }

    private fun quotes(request: QuotesRequest): Deferred<Resource<Vehicles>> {
        return runBlocking {
            val quoteIdResult = apiTemplate.quotes(request).await()
            when (quoteIdResult) {
                is Resource.Success -> quotes(quoteIdResult.data)
                is Resource.Failure -> async { Resource.Failure<Vehicles>(quoteIdResult.error) }
            }
        }
    }

    private fun quotes(quoteId: QuoteId): Deferred<Resource<Vehicles>> {
        this.quoteId = quoteId
        return apiTemplate.quotes(quoteId.quoteId)
    }

}
