package com.karhoo.sdk.api.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.KarhooApi
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class VerifyQuoteIntegrationTest {

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
     * Given:   Verify quote is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun verifyQuoteSuccess() {
        serverRobot {
            verifyQuotesResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.QUOTE)
        }
        var result: Quote? = null

        KarhooApi.quotesService.verifyQuotes(ServerRobot.QUOTE_ID).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        Assertions.assertThat(result).isEqualTo(ServerRobot.QUOTE)
    }

    /**
     * Given:   Verify quote is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/

    @Test
    fun invalidJsonWhenRequestingVerifyQuote() {
        serverRobot {
            verifyQuotesResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.verifyQuotes(ServerRobot.QUOTE_ID).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        Assertions.assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Verify quote is requested
     * When:    Error 401 but with error payload
     * Then:    An error should be returned
     **/

    @Test
    fun invalidSessionTokenWhenRequestingVerifyQuote() {
        serverRobot {
            verifyQuotesResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, response = ServerRobot.GENERAL_ERROR)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.verifyQuotes(ServerRobot.QUOTE_ID).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        Assertions.assertThat(result).isEqualTo(KarhooError.GeneralRequestError)
    }

    /**
     * Given:   Verify quote is requested
     * When:    Success 201 but with no body
     * Then:    An error should be returned
     **/

    @Test
    fun noBodyErrorWhenRequestingVerifyQuote() {
        serverRobot {
            verifyQuotesResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.NO_BODY)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.verifyQuotes(ServerRobot.QUOTE_ID).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        Assertions.assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Verify Quote is requested
     * When:    Error 401 but with empty payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithEmptyBodyWhenRequestingVerifyQuote() {
        serverRobot {
            verifyQuotesResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, response = ServerRobot.EMPTY)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.verifyQuotes(ServerRobot.QUOTE_ID).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        Assertions.assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Verify Quote is requested
     * When:    Error 401 but with empty payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithInvalidJsonyWhenRequestingVerifyQuote() {
        serverRobot {
            verifyQuotesResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, response = ServerRobot.INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.verifyQuotes(ServerRobot.QUOTE_ID).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        Assertions.assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Verify quote is requested
     * When:    The response takes too long
     * Then:    The timeout error should be returned
     **/

    @Test
    fun timeoutErrorResponseWhenRequestingVerifyQuote() {
        serverRobot {
            verifyQuotesResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.INVALID_JSON, delayInMillis = 20000)
        }
        var result: KarhooError? = null

        KarhooApi.quotesService.verifyQuotes(ServerRobot.QUOTE_ID).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        Assertions.assertThat(result).isEqualTo(KarhooError.Timeout)
    }
}