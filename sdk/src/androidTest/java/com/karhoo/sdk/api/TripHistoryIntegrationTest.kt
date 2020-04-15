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
import com.karhoo.sdk.api.util.ServerRobot.Companion.TRIP_LIST_COMPLETED
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TripHistoryIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

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
     * Given:   Trip History is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun tripHistorySuccess() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_CREATED, response = TRIP_LIST_COMPLETED)
        }

        var result: List<TripInfo>? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)

        val expected = TRIP_LIST_COMPLETED.bookings[0]

        //Issue with comparing date due to Gson parsing milliseconds. Not an issue and is covered in other tests.
        assertThat(latch.count).isZero()
        assertEquals(expected.destination, result!![0].destination)
        assertEquals(expected.origin, result!![0].origin)
        assertEquals(expected.displayTripId, result!![0].displayTripId)
        assertEquals(expected.meetingPoint, result!![0].meetingPoint)
        assertEquals(expected.quote, result!![0].quote)
        assertEquals(expected.tripId, result!![0].tripId)
    }

    /**
     * Given:   Trip History is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenRequestingTripInfoReturnsBlankTripInfo() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_CREATED, response = INVALID_DATA)
        }

        var result: List<TripInfo>? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(TestData.BLANK_TRIP_HISTORY)
    }

    /**
     * Given:   Trip History is requested
     * When:    Success 201 but with bad json
     * Then:    An error should be returned
     **/
    @Test
    fun badJsonSuccessReturnsError() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_CREATED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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
     * Given:   Trip History is requested
     * When:    Success 204 but with no body
     * Then:    An error should be returned
     **/
    @Test
    fun noBodyReturnsError() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_CREATED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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
     * Given:   Trip History is requested
     * When:    Error 401 with error payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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
     * Given:   Trip History is requested
     * When:    Error 401 with no body payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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
     * Given:   Trip History is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_UNAUTHORIZED, response = EMPTY)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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
     * Given:   Trip History is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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
     * Given:   Trip History is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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
     * Given:   Trip History is requested
     * When:    The trip history response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            upcomingRidesResponse(code = HTTP_UNAUTHORIZED, response = INVALID_DATA, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.search(TestData.TRIP_SEARCH).execute {
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