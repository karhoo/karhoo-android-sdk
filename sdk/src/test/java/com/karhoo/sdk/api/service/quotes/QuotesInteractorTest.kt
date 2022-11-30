package com.karhoo.sdk.api.service.quotes

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuoteStatus
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.model.Vehicles
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.QuotesRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.junit.MockitoJUnitRunner
import java.util.Date
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class QuotesInteractorTest {

    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val context: CoroutineContext = Dispatchers.Unconfined
    private val applicationContext: Context = mock()

    private lateinit var interactor: QuotesInteractor

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = QuotesInteractor(
                context = context,
                apiTemplate = apiTemplate,
                credentialsManager = credentialsManager)
    }

    /**
     * Given:   A request is made to get quotes
     * When:    The request responds successfully
     * Then:    The response should have quotes
     */
    @Test
    fun `quotes returns successful response`() {
        val quotesList = QuoteList(categories = mapOf(), id = QuoteId("1234567"), status = QuoteStatus.PROGRESSING, validity = 10)
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(ArgumentMatchers.anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(Vehicles(
                        status = QuoteStatus.PROGRESSING, validity = 10))))

        interactor.quotesSearch = quotesSearch

        var returnedQuotesList: QuoteList? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedQuotesList = result.data
                    is Resource.Failure -> Assert.fail(result.error.internalMessage)
                }
            }
            delay(5)
        }

        Assert.assertEquals(quotesList, returnedQuotesList)
    }

    /**
     * Given:
     * When:    A quotes request is made
     * Then:    The error is returned
     **/
    @Test
    fun `when there is an issue with quotes and error is returned`() {
        val expectedError = KarhooError.InternalSDKError
        var shouldBeNull: QuoteList? = null
        var error: KarhooError? = null

        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(ArgumentMatchers.anyString()))
                .thenReturn(CompletableDeferred(Resource.Failure(expectedError)))

        interactor.quotesSearch = quotesSearch

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(100)
        }

        junit.framework.Assert.assertNull(shouldBeNull)
        junit.framework.Assert.assertEquals(expectedError, error)
    }

    /**
     * Given:   A quote id request is made
     * When:    There is an issue with the requests
     * Then:    The error is returned
     **/
    @Test
    fun `when there is an issue with quote id and error is returned`() {
        val expectedError = KarhooError.InternalSDKError
        var shouldBeNull: QuoteList? = null
        var error: KarhooError? = null

        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Failure(expectedError)))

        interactor.quotesSearch = quotesSearch

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(100)
        }

        Assert.assertNull(shouldBeNull)
        Assert.assertEquals(expectedError, error)
    }

    /**
     * Given:   A second request is made
     * When:    The request is executed
     * Then:    Only the quotes endpoint should fire
     **/
    @Test
    fun `repeated requests uses the same quote id`() {
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(ArgumentMatchers.anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(Vehicles())))

        interactor = QuotesInteractor(
                context = context,
                apiTemplate = apiTemplate,
                credentialsManager = credentialsManager)
        interactor.quotesSearch = quotesSearch
        runBlocking {
            interactor.execute { }
            delay(15)
            interactor.execute { }
            interactor.execute { }
            interactor.execute { }
            interactor.execute { }
            delay(100)
        }

        verify(apiTemplate, times(1)).quotes(any<QuotesRequest>())
        verify(apiTemplate, times(5)).quotes("1234567")
    }

    /**
     * Given:   A second request is made
     * When:    The request is executed
     * Then:    Only the quotes endpoint should fire
     **/
    @Test
    fun `quote id is cleared when a quote status of completed is returned`() {
        val progressingQuote = CompletableDeferred(Resource.Success(Vehicles()))
        val completedQuote = CompletableDeferred(Resource.Success(Vehicles(status = QuoteStatus.COMPLETED)))
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("7654321"))))
        whenever(apiTemplate.quotes(ArgumentMatchers.anyString()))
                .thenReturn(progressingQuote)
                .thenReturn(progressingQuote)
                .thenReturn(completedQuote)
                .thenReturn(progressingQuote)

        interactor = QuotesInteractor(
                context = context,
                apiTemplate = apiTemplate,
                credentialsManager = credentialsManager)
        interactor.quotesSearch = quotesSearch
        runBlocking {
            interactor.execute { }
            delay(15)
            interactor.execute { }
            interactor.execute { }
            interactor.execute { }
            delay(15)
            interactor.execute { }
            delay(100)
        }

        verify(apiTemplate, times(2)).quotes(any<QuotesRequest>())
        verify(apiTemplate, times(3)).quotes("1234567")
        verify(apiTemplate, times(2)).quotes("7654321")
    }

    /**
     * Given:   No quote search is set
     * When:    Searching for quotes
     * Then:    An error is returned
     **/
    @Test
    fun `no quotes search set returns an error`() {
        val expectedError = KarhooError.InternalSDKError
        var shouldBeNull: QuoteList? = null
        var error: KarhooError? = null

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        Assert.assertNull(shouldBeNull)
        Assert.assertEquals(expectedError, error)
    }

    /**
     * Given:   A request is made for an asap
     * When:    there is no date scheduled
     * Then:    The request should be fired with out the date
     **/
    @Test
    fun `no date scheduled gets executed`() {
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(ArgumentMatchers.anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(Vehicles())))

        interactor.quotesSearch = quotesSearchNoDate
        runBlocking {
            interactor.execute { }
            delay(5)
        }
        verify(apiTemplate).quotes(any<QuotesRequest>())
        verify(apiTemplate).quotes(ArgumentMatchers.anyString())

    }

    companion object {
        val origin = LocationInfo(placeId = "1234ABZ", position = Position(latitude = 0.1,
                                                                           longitude = 0.2))
        val destination = LocationInfo(placeId = "5678ZXA", position = Position(latitude = 0.3,
                                                                                longitude = 0.4))
        val dateScheduled = Date()

        val quotesSearch = QuotesSearch(origin, destination, dateScheduled)
        val quotesSearchNoDate = QuotesSearch(origin, destination, null)
    }
}
