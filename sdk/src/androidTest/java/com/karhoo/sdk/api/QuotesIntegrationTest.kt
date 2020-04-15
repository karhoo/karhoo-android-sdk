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
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.client.APITemplate.Companion.identifierId
import com.karhoo.sdk.api.network.observable.Observer
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ResponseUtils
import com.karhoo.sdk.api.util.ServerRobot
import com.karhoo.sdk.api.util.ServerRobot.Companion.AVAILABILITIES
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.K3001_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.QUOTES
import com.karhoo.sdk.api.util.ServerRobot.Companion.QUOTE_ID
import com.karhoo.sdk.api.util.ServerRobot.Companion.QUOTE_LIST_EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.VEHICLES
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.junit.After
import org.junit.Assert.assertEquals
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
            authRefreshResponse(code = HTTP_OK, response = ServerRobot.TOKEN)
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   A quote search has been made
     * When:    Error 401 with valid error json responses for Availabilities
     * Then:    The correctly parsed error should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun availabilitiesErrorResponseGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            availabilitiesResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
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
        assertEquals(KarhooError.GeneralRequestError, result)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
            quotesResponse(code = HTTP_OK, response = VEHICLES, quoteId = QUOTE_ID.quoteId)
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
        assertEquals(QUOTES, result)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with invalid data responses for Availabilities
     * Then:    an unexpected error was returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun availabilitiesInvalidDataReturnsError() {
        val latch = CountDownLatch(1)

        serverRobot {
            availabilitiesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_DATA)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
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
                    latch.await(2, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }

        assertEquals(0, latch.count)
        assertEquals(result, KarhooError.Unexpected)
    }

    /**
     * Given:   A quote search has been made
     * When:    Success 200 but with bad json responses for Availabilities
     * Then:    An unexpected KarhooError should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun availabilitiesBadJsonSuccessReturnsUnexpectedError() {
        val latch = CountDownLatch(1)

        serverRobot {
            availabilitiesResponse(code = HTTP_OK, response = INVALID_JSON)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
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
     * When:    Success 200 but with no body responses for Availabilities
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun availabilitiesBlankBodyThrowsUnexpectedError() {
        val latch = CountDownLatch(1)

        serverRobot {
            availabilitiesResponse(code = HTTP_OK, response = NO_BODY)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
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
     * When:    Error 401 with no body responses for Availabilities
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun availabilitiesErrorResponseWithNoBodyGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            availabilitiesResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
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
     * When:    Error 401 with empty payload responses for Availabilities
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun availabilitiesErrorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            availabilitiesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
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
     * When:    Error 401 with valid error json responses for quoteListId
     * Then:    The correctly parsed error should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun quoteListIdErrorResponseGetsParsedIntoKarhooError() {
        val latch = CountDownLatch(1)

        serverRobot {
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
            quoteIdResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
            quotesResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
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
    fun quoteListInvalidDataReturnsEmptyCategories() {
        val latch = CountDownLatch(4)

        serverRobot {
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID)
            quotesResponse(code = HTTP_OK, response = INVALID_DATA)
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

        assertEquals(0, latch.count)
        assertEquals(result, QUOTE_LIST_EMPTY)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES)
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

        val availability = ResponseUtils(httpCode = HTTP_OK, response = Gson().toJson(AVAILABILITIES)).createResponse()
        val quoteListId = ResponseUtils(httpCode = HTTP_OK, response = Gson().toJson(QUOTE_ID)).createResponse()
        val quotesListSuccess = ResponseUtils(httpCode = HTTP_OK, response = Gson().toJson(VEHICLES)).createResponse()
        val quotesListError = ResponseUtils(httpCode = HTTP_UNAUTHORIZED, response = Gson().toJson(K3001_ERROR)).createResponse()

        val scenario = "ScenarioOne"
        val stageTwo = "StageTwo"

        givenThat(post(urlEqualTo(APITemplate.AVAILABILITY_METHOD))
                .inScenario(scenario)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(availability))

        givenThat(post(urlEqualTo(APITemplate.QUOTE_REQUEST_METHOD))
                .inScenario(scenario)
                .willReturn(quoteListId))

        givenThat(get(urlEqualTo(APITemplate.QUOTES_METHOD.replace("{$identifierId}", TestData.QUOTE_LIST_ID)))
                .inScenario(scenario)
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo(stageTwo)
                .willReturn(quotesListError))

        givenThat(get(urlEqualTo(APITemplate.QUOTES_METHOD.replace("{$identifierId}", TestData.QUOTE_LIST_ID)))
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
            availabilitiesResponse(code = HTTP_OK, response = AVAILABILITIES, delayInMillis = 2000)
            quoteIdResponse(code = HTTP_OK, response = QUOTE_ID, delayInMillis = 2000)
            quotesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, delayInMillis = 2000)
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
                    subscribe(observer, 1000L)
                    latch.await(5000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(result, KarhooError.Timeout)
    }

}