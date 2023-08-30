package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.network.observable.Observer
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.BOOKING_ID
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.TRIP_STATE
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TripStateIntegrationTest {

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
     * Given:   A booking id has been polled for tripstate
     * When:    Success 200 responses
     * Then:    The correctly parsed trip state is returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun tripStatusPollingSuccess() {
        serverRobot {
            tripStatusResponse(code = HTTP_OK, response = TRIP_STATE)
        }

        var result: TripState? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }

                    else -> {}
                }
            }

        }

        KarhooApi.tripService.status(BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1000L, TimeUnit.MILLISECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(TRIP_STATE)
    }

    /**
     * Given:   A booking id has been polled for tripstate
     * When:    Error 401 with valid error json responses
     * Then:    The correctly parsed error should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            tripStatusResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }

                    else -> {}
                }
            }
        }

        KarhooApi.tripService.status(BOOKING_ID)
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
     * Given:   A booking id has been polled for tripstate
     * When:    Success 200 but with invalid data responses
     * Then:    an empty drivertrackinginfo should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun invalidDataReturnsEmptyObject() {
        serverRobot {
            tripStatusResponse(code = HTTP_OK, response = INVALID_DATA)
        }

        var result: TripState? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }

                    else -> {}
                }
            }

        }

        KarhooApi.tripService.status(BOOKING_ID)
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
     * Given:   A booking id has been polled for tripstate
     * When:    Success 200 but with bad json responses
     * Then:    An unexpected KarhooError should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun badJsonSuccessReturnsUnexpectedError() {
        serverRobot {
            tripStatusResponse(code = HTTP_OK, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }

                    else -> {}
                }
            }
        }

        KarhooApi.tripService.status(TestData.BOOKING_ID)
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
     * Given:   A booking id has been polled for tripstate
     * When:    Success 200 but with no body responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun blankBodyThrowsUnexpectedError() {
        serverRobot {
            tripStatusResponse(code = HTTP_OK, response = NO_BODY)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }

                    else -> {}
                }
            }
        }

        KarhooApi.tripService.status(TestData.BOOKING_ID)
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
     * Given:   A booking id has been polled for tripstate
     * When:    Error 401 with no body responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            tripStatusResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }

                    else -> {}
                }
            }
        }

        KarhooApi.tripService.status(BOOKING_ID)
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
     * Given:   A booking id has been polled for tripstate
     * When:    Error 401 with empty payload responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            tripStatusResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }

                    else -> {}
                }
            }
        }

        KarhooApi.tripService.status(BOOKING_ID)
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
     * Given:   A booking id has been polled for tripstate
     * When:    The trip response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            tripStatusResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<TripState>> {
            override fun onValueChanged(value: Resource<TripState>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }

                    else -> {}
                }
            }
        }

        KarhooApi.tripService.status(TestData.BOOKING_ID)
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