package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.observable.Observer
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.BOOKING_ID
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.TRIP_DER
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
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
class TrackTripIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch: CountDownLatch = CountDownLatch(4)

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
     * Given:   A booking id has been polled for tripinfo
     * When:    Success 200 responses
     * Then:    The correctly parsed driver tracking info is returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun trackTripPollingSuccess() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_OK, response = TRIP_DER, trip = BOOKING_ID)
        }

        var result: TripInfo? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }
                }
            }

        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .subscribe(observer, 300L)

        latch.await(1000L, TimeUnit.MILLISECONDS)

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .unsubscribe(observer)


        assertThat(latch.count).isZero()
        //Issue with comparing date due to Gson parsing milliseconds. Not an issue and is covered in other tests.
        assertEquals(TRIP_DER.destination, result?.destination)
        assertEquals(TRIP_DER.origin, result?.origin)
        assertEquals(TRIP_DER.displayTripId, result?.displayTripId)
        assertEquals(TRIP_DER.meetingPoint, result?.meetingPoint)
        assertEquals(TRIP_DER.quote, result?.quote)
        assertEquals(TRIP_DER.tripId, result?.tripId)
    }

    /**
     * Given:   A booking id has been polled for tripinfo
     * When:    Error 401 with valid error json responses
     * Then:    The correctly parsed error should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun trackTripErrorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.GeneralRequestError)
    }

    /**
     * Given:   A booking id has been polled for tripinfo
     * When:    Success 200 but with invalid data responses
     * Then:    an empty drivertrackinginfo should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun trackTripInvalidDataReturnsEmptyObject() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_OK, response = INVALID_DATA, trip = BOOKING_ID)
        }

        var result: TripInfo? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }
                }
            }

        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isNotNull
    }

    /**
     * Given:   A booking id has been polled for tripinfo
     * When:    Success 200 but with bad json responses
     * Then:    An unexpected KarhooError should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun badJsonSuccessReturnsUnexpectedError() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_OK, response = INVALID_JSON, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   A booking id has been polled for tripinfo
     * When:    Success 200 but with no body responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun blankBodyThrowsUnexpectedError() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_OK, response = NO_BODY, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   A booking id has been polled for tripinfo
     * When:    Error 401 with no body responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   A booking id has been polled for tripinfo
     * When:    Error 401 with empty payload responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   A booking id has been polled for tripinfo
     * When:    The trip response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            bookingDetailsResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, trip = BOOKING_ID, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripInfo>> {
            override fun onValueChanged(value: Resource<TripInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.tripService.trackTrip(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(2000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Timeout)
    }

}