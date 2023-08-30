package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.testrunner.TestSDKConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CancelIntegrationTest {


    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = TestSDKConfig(context =
                                                                               InstrumentationRegistry.getInstrumentation().context,
                                                                               authenticationMethod = AuthenticationMethod.KarhooUser()))
        serverRobot {
            successfulToken()
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   Cancel trip is requested
     * When:    It is a guest booking
     * And:     Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun cancelGuestBookingTripSuccess() {
        KarhooSDKConfigurationProvider.setConfig(configuration = TestSDKConfig(context =
                                                                               InstrumentationRegistry.getInstrumentation().context,
                                                                               authenticationMethod = AuthenticationMethod.Guest("identifier", "referer", "organisationId")))
        serverRobot {
            cancelGuestBookingResponse(code = HTTP_NO_CONTENT, response = EMPTY, trip = TRIP_IDENTIFIER)
        }

        var result: Void? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun cancelTripSuccess() {
        serverRobot {
            cancelResponse(code = HTTP_NO_CONTENT, response = EMPTY, trip = TRIP_IDENTIFIER)
        }

        var result: Void? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Success 204 but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenRequestingCancellationReturnsSuccessfully() {
        serverRobot {
            cancelResponse(code = HTTP_NO_CONTENT, response = INVALID_DATA, trip = TRIP_IDENTIFIER)
        }

        var result: Void? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Success 204 but with bad json
     * Then:    A blank object should be returned
     **/
    @Test
    fun badJsonSuccessReturnsBlankResult() {
        serverRobot {
            cancelResponse(code = HTTP_NO_CONTENT, response = INVALID_JSON, trip = TRIP_IDENTIFIER)
        }

        var result: Void? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Success 204 but with no body
     * Then:    A blank object should be returned
     **/
    @Test
    fun blankBodyReturnsDefaultObject() {
        serverRobot {
            cancelResponse(code = HTTP_NO_CONTENT, response = NO_BODY, trip = TRIP_IDENTIFIER)
        }

        var result: Void? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Error 401 with error payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            cancelResponse(code = HTTP_BAD_REQUEST, response = GENERAL_ERROR, trip = TRIP_IDENTIFIER)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.GeneralRequestError)
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Error 401 with no body payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            cancelResponse(code = HTTP_BAD_REQUEST, response = NO_BODY, trip = TRIP_IDENTIFIER)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            cancelResponse(code = HTTP_BAD_REQUEST, response = EMPTY, trip = TRIP_IDENTIFIER)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            cancelResponse(code = HTTP_BAD_REQUEST, response = INVALID_JSON, trip = TRIP_IDENTIFIER)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Cancel trip is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            cancelResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA, trip = TRIP_IDENTIFIER)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Cancel is requested
     * When:    The trip response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            cancelResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA, trip = TRIP_IDENTIFIER, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.tripService.cancel(TestData.CANCEL).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Timeout)
    }

    companion object {
        const val TRIP_IDENTIFIER = "1234"
    }

}