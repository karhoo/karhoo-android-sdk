package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.TOKEN
import com.karhoo.sdk.api.util.ServerRobot.Companion.TRIP_REQUESTED_DETAILS
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_OK
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class BookIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setUp() {
        serverRobot {
            authRefreshResponse(code = HTTP_OK, response = TOKEN)
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   Book Trip is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun bookSuccess() {
        serverRobot {
            bookingResponse(code = HTTP_CREATED, response = TRIP_REQUESTED_DETAILS)
        }

        var result: TripInfo? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result?.tripId).isEqualTo(TRIP_REQUESTED_DETAILS.tripId)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenRequestingTripInfoReturnsBlankTripInfo() {
        serverRobot {
            bookingResponse(code = HTTP_CREATED, response = INVALID_DATA)
        }

        var result: TripInfo? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(TestData.TRIP_INFO_BLANK)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Success 201 but with bad json
     * Then:    An error should be returned
     **/
    @Test
    fun badJsonSuccessReturnsError() {
        serverRobot {
            bookingResponse(code = HTTP_CREATED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Success 204 but with no body
     * Then:    An error should be returned
     **/
    @Test
    fun noBodyReturnsError() {
        serverRobot {
            bookingResponse(code = HTTP_CREATED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Error 401 with error payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            bookingResponse(code = HTTP_BAD_REQUEST, response = GENERAL_ERROR)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.GeneralRequestError)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Error 401 with no body payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            bookingResponse(code = HTTP_BAD_REQUEST, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            bookingResponse(code = HTTP_BAD_REQUEST, response = EMPTY)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            bookingResponse(code = HTTP_BAD_REQUEST, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Book Trip is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            bookingResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Book Trip is requested
     * When:    The trip response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            bookingResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.book(TestData.BOOK_TRIP).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Timeout)
    }

}