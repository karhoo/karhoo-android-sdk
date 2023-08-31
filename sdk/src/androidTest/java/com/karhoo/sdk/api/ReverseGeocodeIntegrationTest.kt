package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.LOCATION_INFO
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
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
class ReverseGeocodeIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch: CountDownLatch = CountDownLatch(1)

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   A position has been reverse geocoded
     * When:    Success 200 but with valid json
     * Then:    The correctly parsed locationinfo should be returned
     **/
    @Test
    fun reverseGeocodeSuccess() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_OK, response = LOCATION_INFO)
        }

        var result: LocationInfo? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)

        assertThat(result).isEqualTo(TestData.LOCATION_INFO)
    }

    /**
     * Given:   A position has been reverse geocoded
     * When:    Error 401 with valid error json
     * Then:    The correctly parsed error should be returned
     **/
    @Test
    fun reverseGeocodErrorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
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
     * Given:   A position has been reverse geocoded
     * When:    Success 200 but with invalid data
     * Then:    an empty locationinfo should be returned
     **/
    @Test
    fun reverseGeocodeInvalidDataReturnsEmptyObject() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_OK, response = INVALID_DATA)
        }

        var result: LocationInfo? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
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
     * Given:   A position has been reverse geocoded
     * When:    Success 200 but with bad json
     * Then:    An unexpected KarhooError should be returned
     **/
    @Test
    fun badJsonSuccessReturnsUnexpectedError() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_OK, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
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
     * Given:   A position has been reverse geocoded
     * When:    Success 200 but with no body
     * Then:    Unexpected KarhooError returned
     **/
    @Test
    fun blankBodyThrowsUnexpectedError() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_OK, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
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
     * Given:   A position has been reverse geocoded
     * When:    Error 401 with no body
     * Then:    Unexpected KarhooError returned
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
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
     * Given:   A position has been reverse geocoded
     * When:    Error 401 with empty payload
     * Then:    Unexpected KarhooError returned
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
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
     * Given:   A position has been reverse geocoded
     * When:    The reverse geo response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            reverseGeocodeResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.reverseGeocode(TestData.POSITION).execute {
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

}