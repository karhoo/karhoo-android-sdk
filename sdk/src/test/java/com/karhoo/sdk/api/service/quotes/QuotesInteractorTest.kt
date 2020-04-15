package com.karhoo.sdk.api.service.quotes

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
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
import junit.framework.Assert
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner
import java.util.Date
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class QuotesInteractorTest {

    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val context: CoroutineContext = Unconfined
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
        val quotesList = QuoteList(categories = mapOf(), id = QuoteId("1234567"))
        whenever(apiTemplate.availabilities(any()))
                .thenReturn(CompletableDeferred(Resource.Success(Categories(listOf()))))
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(Vehicles())))

        interactor.quotesSearch = quotesSearch

        var returnedQuotesList: QuoteList? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedQuotesList = result.data
                    is Resource.Failure -> fail(result.error.internalMessage)
                }
            }
            delay(5)
        }

        assertEquals(quotesList, returnedQuotesList)
    }

    /**
     * Given:   There is an issue with the request
     * When:    An availability request is made
     * Then:    The error is returned
     **/
    @Test
    fun `when there is an issue with availability an error is returned`() {
        val expectedError = KarhooError.CouldNotGetAvailabilityNoCategories
        var shouldBeNull: QuoteList? = null
        var error: KarhooError? = null

        whenever(apiTemplate.availabilities(any()))
                .thenReturn(CompletableDeferred(Resource.Failure(expectedError)))
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(Vehicles())))

        interactor.quotesSearch = quotesSearch

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        assertNull(shouldBeNull)
        assertEquals(expectedError, error)
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

        whenever(apiTemplate.availabilities(any()))
                .thenReturn(CompletableDeferred(Resource.Success(Categories(listOf()))))
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(anyString()))
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
     * Given:   A quote id request is made
     * When:    There is an issue with the requests
     * Then:    The error is returned
     **/
    @Test
    fun `when there is an issue with quote id and error is returned`() {
        val expectedError = KarhooError.InternalSDKError
        var shouldBeNull: QuoteList? = null
        var error: KarhooError? = null

        whenever(apiTemplate.availabilities(any()))
                .thenReturn(CompletableDeferred(Resource.Success(Categories(listOf()))))
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

        assertNull(shouldBeNull)
        assertEquals(expectedError, error)
    }

    /**
     * Given:   A request is made to get quotes
     * When:    There is a destination and origin
     * Then:    The availability should return a map of categories
     */
    @Test
    fun `availability returns categories`() {
        val quotesList = QuoteList(categories = mapOf(pair = Pair("MPV", listOf())), id = QuoteId("1234567"))
        val categoryList = listOf("MPV")
        whenever(apiTemplate.availabilities(any()))
                .thenReturn(CompletableDeferred(Resource.Success(Categories(categoryList))))
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(Vehicles())))

        interactor.quotesSearch = quotesSearch

        var returnedQuotesList: QuoteList? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedQuotesList = result.data
                    is Resource.Failure -> fail(result.error.internalMessage)
                }
            }
            delay(5)
        }

        assertEquals(quotesList.categories, returnedQuotesList?.categories)
        assertTrue(quotesList.categories.containsKey(categoryList[0]))
    }

    /**
     * Given:   A second request is made
     * When:    The request is executed
     * Then:    Only the quotes endpoint should fire
     **/
    @Ignore("Seems to be flaky")
    @Test
    fun `repeated requests uses the same quote id`() {
        whenever(apiTemplate.availabilities(any()))
                .thenReturn(CompletableDeferred(Resource.Success(Categories(listOf()))))
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(anyString()))
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

        verify(apiTemplate, times(1)).availabilities(any())
        verify(apiTemplate, times(1)).quotes(any<QuotesRequest>())
        verify(apiTemplate, times(5)).quotes(anyString())
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

        assertNull(shouldBeNull)
        assertEquals(expectedError, error)
    }

    /**
     * Given:   A request is made for an asap
     * When:    there is no date scheduled
     * Then:    The request should be fired with out the date
     **/
    @Test
    fun `no date scheduled gets executed`() {
        whenever(apiTemplate.availabilities(any()))
                .thenReturn(CompletableDeferred(Resource.Success(Categories(listOf()))))
        whenever(apiTemplate.quotes(any<QuotesRequest>()))
                .thenReturn(CompletableDeferred(Resource.Success(QuoteId("1234567"))))
        whenever(apiTemplate.quotes(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(Vehicles())))

        interactor.quotesSearch = quotesSearchNoDate
        runBlocking {
            interactor.execute { }
            delay(5)
        }
        verify(apiTemplate).availabilities(any())
        verify(apiTemplate).quotes(any<QuotesRequest>())
        verify(apiTemplate).quotes(anyString())

    }

    companion object {
        val origin = LocationInfo(placeId = "1234ABZ")
        val destination = LocationInfo(placeId = "5678ZXA")
        val dateScheduled = Date()

        val quotesSearch = QuotesSearch(origin, destination, dateScheduled)
        val quotesSearchNoDate = QuotesSearch(origin, destination, null)
    }

}