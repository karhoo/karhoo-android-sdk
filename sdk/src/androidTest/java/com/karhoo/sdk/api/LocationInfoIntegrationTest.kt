package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.network.request.LocationInfoRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.LOCATION_INFO
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
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
class LocationInfoIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setUp() {
        serverRobot {
            authRefreshResponse(code = HTTP_OK, response = ServerRobot.TOKEN)
            authUserResponse(code = HTTP_OK, response = ServerRobot.USER_INFO)
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   Location info is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun locationInfoSuccess() {
        serverRobot {
            addressDetails(code = HTTP_OK, response = LOCATION_INFO)
        }
        var result: LocationInfo? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("qwewuihfcsd", "fake_session_token")).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(TestData.LOCATION_INFO)
    }

    /**
     * Given:   Location info is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenRequestingLocationInfoReturnsBlankLocationInfo() {
        serverRobot {
            addressDetails(code = HTTP_OK, response = INVALID_DATA)
        }

        var result: LocationInfo? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("jkfsdhnvsdjn", "fake_session_token")).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result?.displayAddress).isNullOrEmpty()
    }

    /**
     * Given:   Location info is requested
     * When:    Error 401 with error payload
     * Then:    An error should be returned
     **/
    @Test
    fun invalidSessionTokenWhenRequestingLocationInfoGetsParsedIntoKarhooError() {
        serverRobot {
            addressDetails(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("jkfsdhnvsdjn", "")).execute {
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
     * Given:   Location info is requested
     * When:    Success 201 but with bad json
     * Then:    An error should be returned
     **/
    @Test
    fun badJsonSuccessReturnsError() {
        serverRobot {
            addressDetails(code = HTTP_OK, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("kdjnfcdjskn", "fake_session_token")).execute {
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
     * Given:   Location info is requested
     * When:    Success 204 but with no body
     * Then:    An error should be returned
     **/
    @Test
    fun noBodyReturnsError() {
        serverRobot {
            addressDetails(code = HTTP_OK, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("kdjnfcdjskn", "fake_session_token")).execute {
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
     * Given:   Location info is requested
     * When:    Error 401 with error payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            addressDetails(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("fkihjbgvdhjv", "fake_session_token")).execute {
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
     * Given:   Location info is requested
     * When:    Error 401 with no body payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            addressDetails(code = HTTP_UNAUTHORIZED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("fkihjbgvdhjv", "fake_session_token")).execute {
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
     * Given:   Location info is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            addressDetails(code = HTTP_UNAUTHORIZED, response = EMPTY)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("fkihjbgvdhjv", "fake_session_token")).execute {
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
     * Given:   Location info is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            addressDetails(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("fkihjbgvdhjv", "fake_session_token")).execute {
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
     * Given:   Location info is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            addressDetails(code = HTTP_UNAUTHORIZED, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("fkihjbgvdhjv", "fake_session_token")).execute {
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
     * Given:   Cancel is requested
     * When:    The trip response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            addressDetails(code = HTTP_OK, response = INVALID_DATA, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.addressService.locationInfo(LocationInfoRequest("fijuhnvbinvb", "fake_session_token")).execute {
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