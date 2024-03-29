package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuoteStatus
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.model.Vehicles
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.QuoteRequestPoint
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
                                                    context: CoroutineContext = Main) :
        BasePollCallInteractor<QuoteList>(true, credentialsManager, apiTemplate, context) {

    internal var quotesSearch: QuotesSearch? = null
    private var vehicles: Vehicles? = null
    internal var quoteId: QuoteId? = null
    internal var locale : String? = null

    override fun createRequest(): Deferred<Resource<QuoteList>> {
        quotesSearch?.let { search ->
            quoteId?.let { quote ->
                return GlobalScope.async {
                    val result = quoteList(quotes(quote).await())
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
                            quotesResource = quotes(createQuoteRequest(search)).await())
                }
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private fun createQuoteRequest(quotesSearch: QuotesSearch): QuotesRequest? {
        val dateScheduled: String? = quotesSearch.dateScheduled?.let {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm").apply {
                timeZone = TimeZone.getTimeZone(quotesSearch.origin.timezone)
            }.format(it)
        }

        val originPosition = quotesSearch.origin.position?.let { it } ?: return null
        val destinationPosition = quotesSearch.destination.position?.let { it } ?: return null

        return QuotesRequest(
                origin = QuoteRequestPoint(latitude = prepareCoordinate(originPosition.latitude),
                                           longitude = prepareCoordinate(originPosition.longitude),
                                           displayAddress = quotesSearch.origin.displayAddress),
                destination = QuoteRequestPoint(latitude = prepareCoordinate(destinationPosition.latitude),
                                                longitude = prepareCoordinate(destinationPosition.longitude),
                                                displayAddress = quotesSearch.destination.displayAddress),
                dateScheduled = dateScheduled)
    }

    private fun prepareCoordinate(coordinate: Double): String{
        var pos = coordinate.toString()
        val indexOfDecimal = pos.indexOf(".")
        var decimals = pos.substring(indexOfDecimal + 1)
        while(decimals.length < 4){
            decimals += "0"
            pos = pos.replaceAfter(".", decimals)
        }
        return pos
    }

    private fun quoteList(quotesResource: Resource<Vehicles>): Resource<QuoteList> {
        when (quotesResource) {
            is Resource.Success -> this.vehicles = quotesResource.data
            is Resource.Failure -> return Resource.Failure(quotesResource.error)
        }

        this.vehicles?.let {

            val quotesMap = mutableMapOf<String, List<Quote>>()
            val categoryNames = it.availability.vehicles.classes

            categoryNames.forEach { category ->
                val filteredVehicles: List<Quote> = it.quotes.filter { quote ->
                    quote.vehicle.vehicleClass ==
                            category
                }
                quotesMap[category] = filteredVehicles
            }

            if (it.status == QuoteStatus.COMPLETED) {
                quoteId = null
            }

            return Resource.Success(QuoteList(id = quoteId ?: QuoteId(), categories = quotesMap,
                                              status = it.status, validity = it.validity))
        } ?: return Resource.Failure(error = KarhooError.InternalSDKError)
    }

    private fun quotes(request: QuotesRequest?): Deferred<Resource<Vehicles>> {
        if (request == null) {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
        return runBlocking {
            val localeVal = if(locale.isNullOrEmpty()) null else locale
            val quoteIdResult : Resource<QuoteId> = localeVal?.let { locale ->
                apiTemplate.quotes(request, locale).await()
            } ?: run {
                apiTemplate.quotes(request).await()
            }

            when (quoteIdResult) {
                is Resource.Success -> quotes(quoteIdResult.data)
                is Resource.Failure -> async { Resource.Failure<Vehicles>(quoteIdResult.error) }
            }
        }
    }

    private fun quotes(quoteId: QuoteId): Deferred<Resource<Vehicles>> {
        this.quoteId = quoteId

        val localeVal = if(locale.isNullOrEmpty()) null else locale
        localeVal?.let { locale ->
            return apiTemplate.quotes(quoteId.quoteId, locale)
        } ?: run {
            return apiTemplate.quotes(quoteId.quoteId)
        }
    }
}
