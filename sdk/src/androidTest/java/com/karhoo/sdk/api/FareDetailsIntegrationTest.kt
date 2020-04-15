package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot
import com.karhoo.sdk.api.util.ServerRobot.Companion.BOOKING_ID
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.FARE
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
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
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FareDetailsIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch: CountDownLatch = CountDownLatch(1)

    fun setUp() {
        serverRobot {
            authTokenResponse(code = HTTP_OK, response = TOKEN,
                              header = Pair("accept", "application/json"))
            authUserResponse(code = HTTP_OK, response = USER_INFO)
            authRefreshResponse(code = HTTP_OK, response = TOKEN)
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   Fare details is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun fareDetailsSuccess() {
        serverRobot {
            fareResponse(code = HTTP_CREATED, response = FARE, tripId = BOOKING_ID)
        }

        var result: Fare? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(TestData.FARE)
    }

    /**
     * Given:   Fare details is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenRequestingLocationInfoReturnsBlankLocationInfo() {
        serverRobot {
            fareResponse(code = HTTP_CREATED, response = INVALID_DATA, tripId = BOOKING_ID)
        }

        var result: Fare? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result?.state).isNullOrEmpty()
    }

    /**
     * Given:   Fare details is requested
     * When:    Error 401 with error payload
     * Then:    An error should be returned
     **/
    @Test
    fun invalidSessionTokenWhenRequestingLocationInfoGetsParsedIntoKarhooError() {
        serverRobot {
            fareResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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
     * Given:   Fare details is requested
     * When:    Success 201 but with bad json
     * Then:    An error should be returned
     **/
    @Test
    fun badJsonSuccessReturnsError() {
        serverRobot {
            fareResponse(code = HTTP_CREATED, response = INVALID_JSON, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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
     * Given:   Fare details is requested
     * When:    Success 204 but with no body
     * Then:    An error should be returned
     **/
    @Test
    fun noBodyReturnsError() {
        serverRobot {
            fareResponse(code = HTTP_CREATED, response = NO_BODY, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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
     * Given:   Fare details is requested
     * When:    Error 401 with error payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            fareResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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
     * Given:   Fare details is requested
     * When:    Error 401 with no body payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            fareResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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
     * Given:   Fare details is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            fareResponse(code = HTTP_UNAUTHORIZED, response = EMPTY, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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
     * Given:   Fare details is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            fareResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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
     * Given:   Fare details is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            fareResponse(code = HTTP_UNAUTHORIZED, response = INVALID_DATA, tripId = BOOKING_ID)
        }

        var result: KarhooError? = null

        KarhooApi.fareService.fareDetails(TestData.BOOKING_ID).execute {
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

}