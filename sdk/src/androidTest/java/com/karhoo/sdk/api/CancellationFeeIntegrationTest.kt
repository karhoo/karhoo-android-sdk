package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.BookingFee
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CancellationFeeIntegrationTest {

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
     * Given:   Cancellation fee on a trip is requested
     * When:    It is a user booking
     * And:     Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun cancellationFeeSuccess() {
        serverRobot {
            cancellationFeeResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.BOOKINGFEE)
        }
        var result: BookingFee? = null

        KarhooApi.tripService.cancellationFee(ServerRobot.BOOKING_ID).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(ServerRobot.BOOKINGFEE)
    }

    /**
     * Given:   Cancellation Fee is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/

    @Test
    fun invalidJsonWhenRequestingCancellationFee() {
        serverRobot {
            cancellationFeeResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.tripService.cancellationFee(ServerRobot.BOOKING_ID).execute {
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
     * Given:   Cancellation Fee is requested
     * When:    Error 401 but with error payload
     * Then:    An error should be returned
     **/

    @Test
    fun invalidSessionTokenWhenRequestingCancellationFee() {
        serverRobot {
            cancellationFeeResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, response = ServerRobot.GENERAL_ERROR)
        }
        var result: KarhooError? = null

        KarhooApi.tripService.cancellationFee(ServerRobot.BOOKING_ID).execute {
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
     * Given:   Cancellation Fee is requested
     * When:    Success 201 but with no body
     * Then:    An error should be returned
     **/

    @Test
    fun noBodyErrorWhenRequestingCancellationFee() {
        serverRobot {
            cancellationFeeResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.NO_BODY)
        }
        var result: KarhooError? = null

        KarhooApi.tripService.cancellationFee(ServerRobot.BOOKING_ID).execute {
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
     * Given:   Cancellation Fee is requested
     * When:    Error 401 but with empty payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithEmptyBodyWhenRequestingCancellationFee() {
        serverRobot {
            cancellationFeeResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, response = ServerRobot.EMPTY)
        }
        var result: KarhooError? = null

        KarhooApi.tripService.cancellationFee(ServerRobot.BOOKING_ID).execute {
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
     * Given:   Cancellation Fee is requested
     * When:    Error 401 but with empty payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithInvalidJsonyWhenRequestingCancellationFee() {
        serverRobot {
            cancellationFeeResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, response = ServerRobot.INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.tripService.cancellationFee(ServerRobot.BOOKING_ID).execute {
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
     * Given:   Cancellation Fee is requested
     * When:    The response takes too long
     * Then:    The timeout error should be returned
     **/

    @Test
    fun timeoutErrorResponseWhenRequestingCancellationFee() {
        serverRobot {
            cancellationFeeResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.INVALID_JSON, delayInMillis = 20000)
        }
        var result: KarhooError? = null

        KarhooApi.tripService.cancellationFee(ServerRobot.BOOKING_ID).execute {
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