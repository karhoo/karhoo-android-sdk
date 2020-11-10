package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.Coverage
import com.karhoo.sdk.api.network.request.CoverageRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.COVERAGE_OK
import com.karhoo.sdk.api.util.ServerRobot.Companion.COVERAGE_REQUEST
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.LAT
import com.karhoo.sdk.api.util.ServerRobot.Companion.LONG
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
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
class CheckCoverageIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setup() {
        serverRobot {
            successfulToken()
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   Check coverage is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun checkCoverageSuccess() {
        serverRobot {
            getCheckCoverageResponse(code = HTTP_OK, response = COVERAGE_OK)
        }
        var result: Coverage? = null

        KarhooApi.quotesService.checkCoverage(COVERAGE_REQUEST).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(COVERAGE_OK)
    }

    /**
     * Given:   Check coverage is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/

    @Test
    fun invalidJsonWhenRequestingCheckCoverage() {
        serverRobot {
            getCheckCoverageResponse(code = HTTP_OK, response = INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.checkCoverage(COVERAGE_REQUEST).execute {
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
     * Given:   Check coverage is requested
     * When:    Error 401 but with error payload
     * Then:    An error should be returned
     **/

    @Test
    fun invalidSessionTokenWhenRequestingCheckCoverage() {
        serverRobot {
            getCheckCoverageResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.checkCoverage(COVERAGE_REQUEST).execute {
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
     * Given:   Check coverage is requested
     * When:    Success 201 but with no body
     * Then:    An error should be returned
     **/

    @Test
    fun noBodyErrorWhenRequestingCheckCoverage() {
        serverRobot {
            getCheckCoverageResponse(code = HTTP_OK, response = NO_BODY)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.checkCoverage(COVERAGE_REQUEST).execute {
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
     * Given:   Check coverage is requested
     * When:    Error 401 but with empty payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithEmptyBodyWhenRequestingCheckCoverage() {
        serverRobot {
            getCheckCoverageResponse(code = HTTP_UNAUTHORIZED, response = EMPTY)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.checkCoverage(COVERAGE_REQUEST).execute {
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
     * Given:   Check coverage is requested
     * When:    Error 401 but with empty payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithInvalidJsonyWhenRequestingCheckCoverage() {
        serverRobot {
            getCheckCoverageResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.checkCoverage(COVERAGE_REQUEST).execute {
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
     * Given:   Check coverage is requested
     * When:    The response takes too long
     * Then:    The timeout error should be returned
     **/

    @Test
    fun timeoutErrorResponseWhenRequestingCheckCoverage() {
        serverRobot {
            getCheckCoverageResponse(code = HTTP_OK, response = INVALID_JSON, delayInMillis = 20000)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.checkCoverage(COVERAGE_REQUEST).execute {
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