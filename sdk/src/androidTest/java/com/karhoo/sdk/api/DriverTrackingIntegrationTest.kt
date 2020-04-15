package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.network.observable.Observer
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.BOOKING_ID
import com.karhoo.sdk.api.util.ServerRobot.Companion.DRIVER_TRACKING
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.TOKEN
import com.karhoo.sdk.api.util.ServerRobot.Companion.USER_INFO
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class DriverTrackingIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch: CountDownLatch = CountDownLatch(4)

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   A booking id has been polled for driver tracking info
     * When:    Success 200 responses
     * Then:    The correctly parsed driver tracking info is returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun driverTrackingPollingSuccess() {
        serverRobot {
            authRefreshResponse(code = HTTP_OK, response = TOKEN)
            driverTrackingResponse(code = HTTP_OK, response = DRIVER_TRACKING, trip = BOOKING_ID)
        }

        var result: DriverTrackingInfo? = null

        val observer = object : Observer<Resource<DriverTrackingInfo>> {
            override fun onValueChanged(value: Resource<DriverTrackingInfo>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }
                }
            }

        }

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(DRIVER_TRACKING)
    }

    /**
     * Given:   A booking id has been polled for driver tracking info
     * When:    Error 401 with valid error json responses
     * Then:    The correctly parsed error should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun driverTrackingErrorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            driverTrackingResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<DriverTrackingInfo>> {
            override fun onValueChanged(value: Resource<DriverTrackingInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.GeneralRequestError)
    }

    /**
     * Given:   A booking id has been polled for driver tracking info
     * When:    Success 200 but with invalid data responses
     * Then:    an empty drivertrackinginfo should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun trackDriverInvalidDataReturnsEmptyObject() {
        serverRobot {
            authRefreshResponse(code = HTTP_OK, response = TOKEN)
            driverTrackingResponse(code = HTTP_OK, response = EMPTY, trip = BOOKING_ID)
        }

        var result: DriverTrackingInfo? = null

        val observer = object : Observer<Resource<DriverTrackingInfo>> {
            override fun onValueChanged(value: Resource<DriverTrackingInfo>) {
                when (value) {
                    is Resource.Success -> {
                        result = value.data
                        latch.countDown()
                    }
                }
            }

        }

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isNotNull
    }

    /**
     * Given:   A booking id has been polled for driver tracking info
     * When:    Success 200 but with bad json responses
     * Then:    An unexpected KarhooError should be returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun badJsonSuccessReturnsUnexpectedError() {
        serverRobot {
            driverTrackingResponse(code = HTTP_OK, response = INVALID_JSON, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<DriverTrackingInfo>> {
            override fun onValueChanged(value: Resource<DriverTrackingInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   A booking id has been polled for driver tracking info
     * When:    Success 200 but with no body responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun blankBodyThrowsUnexpectedError() {
        serverRobot {
            driverTrackingResponse(code = HTTP_OK, response = NO_BODY, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<DriverTrackingInfo>> {
            override fun onValueChanged(value: Resource<DriverTrackingInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   A booking id has been polled for driver tracking info
     * When:    Error 401 with no body responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            driverTrackingResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<DriverTrackingInfo>> {
            override fun onValueChanged(value: Resource<DriverTrackingInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   A booking id has been polled for driver tracking info
     * When:    Error 401 with empty payload responses
     * Then:    Unexpected KarhooError returned
     * And:     The endpoint was polled the correct number of times
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            driverTrackingResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, trip = BOOKING_ID)
        }

        var result: KarhooError? = null

        val observer = object : Observer<Resource<DriverTrackingInfo>> {
            override fun onValueChanged(value: Resource<DriverTrackingInfo>) {
                when (value) {
                    is Resource.Failure -> {
                        result = value.error
                        latch.countDown()
                    }
                }
            }
        }

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID)
                .observable()
                .apply {
                    subscribe(observer, 300L)
                    latch.await(1, TimeUnit.SECONDS)
                    unsubscribe(observer)
                }


        assertThat(latch.count).isZero()
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Driver tracking is requested
     * When:    The driver tracking response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            driverTrackingResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, trip = BOOKING_ID, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.driverTrackingService.trackDriver(TestData.BOOKING_ID).execute {
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