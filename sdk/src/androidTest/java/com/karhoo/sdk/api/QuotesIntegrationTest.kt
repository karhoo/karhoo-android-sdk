package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.givenThat
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.google.gson.Gson
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuoteSource
import com.karhoo.sdk.api.model.QuoteType
import com.karhoo.sdk.api.network.client.APITemplate.Companion.QUOTES_REQUEST_METHOD
import com.karhoo.sdk.api.network.client.APITemplate.Companion.QUOTES_METHOD
import com.karhoo.sdk.api.network.client.APITemplate.Companion.IDENTIFIER_ID
import com.karhoo.sdk.api.network.observable.Observer
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ResponseUtils
import com.karhoo.sdk.api.util.ServerRobot.Companion.AVAILABILITY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.K3001_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.QUOTE_ID
import com.karhoo.sdk.api.util.ServerRobot.Companion.QUOTE
import com.karhoo.sdk.api.util.ServerRobot.Companion.VEHICLES
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class QuotesIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    @Before
    fun setUp() {
        serverRobot {
            successfulToken()
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 responses
     * Then:    The correctly parsed quotes list is returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun tripStatusPollingSuccess() {
        val latch = CountDownLatch(4)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID, endpoint = QUOTES_REQUEST_METHOD)
            quotesResponse(code = HTTP_OK, response = VEHICLES, endpoint = QUOTES_METHOD,
                            quoteId = QUOTE_ID.quoteId)
        }

        var result: QuoteList? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }
                    is Resource.Failure -> {
                        print(value.error)
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(200, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertEquals(0, latch.count)
        assertNotNull(result)
        assertEquals(AVAILABILITY.vehicles.classes.size, result?.categories?.size)

        val saloonQuotes = result?.categories?.get("Saloon")
        assertEquals(VEHICLES.quotes.size, saloonQuotes?.size)
        assertEquals(QUOTE.id, saloonQuotes?.get(0)?.id)
        assertEquals(QUOTE.fleet, saloonQuotes?.get(0)?.fleet)
        assertEquals(QUOTE.quoteSource, saloonQuotes?.get(0)?.quoteSource)
        assertEquals(QUOTE.quoteType, saloonQuotes?.get(0)?.quoteType)
        assertEquals("someOtherQuoteId", saloonQuotes?.get(1)?.id)
        assertEquals("someOtherFleetId", saloonQuotes?.get(1)?.fleet?.id)
        assertEquals(QuoteSource.FLEET, saloonQuotes?.get(1)?.quoteSource)
        assertEquals(QuoteType.METERED, saloonQuotes?.get(1)?.quoteType)
    }

    /**
     * Given:   A quote search has been made
     * When:    Error 401 with valid error json responses for quoteListId
     * Then:    The correctly parsed error should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListIdErrorResponseGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR, endpoint = QUOTES_REQUEST_METHOD)
            quotesResponse(code = HTTP_OK, response = VEHICLES, endpoint = QUOTES_METHOD)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.GeneralRequestError)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with invalid data responses for quoteListId
     * Then:    Unexpected error returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListIdInvalidDataReturnsKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = INVALID_DATA)
            quotesResponse(code = HTTP_OK, response = VEHICLES)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }

        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with bad json responses for quoteListId
     * Then:    An unexpected KarhooError should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListIdBadJsonSuccessReturnsUnexpectedError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = INVALID_JSON)
            quotesResponse(code = HTTP_OK, response = VEHICLES)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with no body responses for quoteListId
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListIdBlankBodyThrowsUnexpectedError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = NO_BODY)
            quotesResponse(code = HTTP_OK, response = VEHICLES)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Error 401 with no body responses for quoteListId
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListIdErrorResponseWithNoBodyGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY)
            quotesResponse(code = HTTP_OK, response = VEHICLES)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Error 401 with empty payload responses for quoteListId
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListIdErrorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
            quotesResponse(code = HTTP_OK, response = VEHICLES)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Error 401 with valid error json responses for quoteList
     * Then:    The correctly parsed error should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListErrorResponseGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID, endpoint = QUOTES_REQUEST_METHOD)
            quotesResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR, endpoint = QUOTES_METHOD)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.GeneralRequestError)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with invalid data responses for quoteList
     * Then:    Unexpected error returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun invalidQuoteDataReturnsEmptyCategoriesList() {
        val latch = CountDownLatch(4)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID, endpoint = QUOTES_REQUEST_METHOD)
            quotesResponse(code = HTTP_OK, response = INVALID_DATA, endpoint = QUOTES_METHOD)
        }

        var result: QuoteList? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }
                }
            }

        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, result?.categories?.size)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with bad json responses for quoteList
     * Then:    An unexpected KarhooError should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListBadJsonSuccessReturnsUnexpectedError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
            quotesResponse(code = HTTP_OK, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with no body responses for quoteList
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListBlankBodyThrowsUnexpectedError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
            quotesResponse(code = HTTP_OK, response = NO_BODY)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Error 401 with no body responses for quoteList
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListErrorResponseWithNoBodyGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
            quotesResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Error 401 with empty payload responses for quoteList
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListErrorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
            quotesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    A could not get estimates error occurs from the quotes endpoint
     * Then:    The request should be made again to get fresh quotes
     */
    @Test
    fun quoteListExpiryRefreshesProcess() {
        val latch = CountDownLatch(1)

        val quoteListId = ResponseUtils(httpCode = HTTP_OK, response = Gson().toJson(QUOTE_ID)).createResponse()
        val quotesListSuccess = ResponseUtils(httpCode = HTTP_OK, response = Gson().toJson(VEHICLES)).createResponse()
        val quotesListError = ResponseUtils(httpCode = HTTP_UNAUTHORIZED, response = Gson().toJson(K3001_ERROR)).createResponse()

        val scenario = "ScenarioOne"
        val stageTwo = "StageTwo"

        givenThat(post(urlEqualTo(QUOTES_REQUEST_METHOD))
                          .inScenario(scenario)
                          .willReturn(quoteListId))

        givenThat(get(urlEqualTo(QUOTES_METHOD.replace("{$IDENTIFIER_ID}", TestData.QUOTE_LIST_ID)))
                          .inScenario(scenario)
                          .whenScenarioStateIs(Scenario.STARTED)
                          .willSetStateTo(stageTwo)
                          .willReturn(quotesListError))

    givenThat(get(urlEqualTo(QUOTES_METHOD.replace("{$IDENTIFIER_ID}", TestData.QUOTE_LIST_ID)))
                          .inScenario(scenario)
                          .whenScenarioStateIs(stageTwo)
                          .willReturn(quotesListSuccess))


        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(500L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.CouldNotGetEstimates)
    }

    /**
     * Given:   A quote search has been made
     * When:    The quote response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        val latch = CountDownLatch(1)

        serverRobot {
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID, delayInMillis = 2000, endpoint =
            QUOTES_REQUEST_METHOD)
            quotesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, endpoint =
            QUOTES_METHOD, delayInMillis = 2000) }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<QuoteList>> {
            override fun onValueChanged(value: Resource<QuoteList>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.quotesService.quotes(TestData.QUOTE_SEARCH)
                .observable()
                .apply {
                    subscribe(observer, 1000L)
                    latch.await(5000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(result, KarhooError.Timeout)
    }

}