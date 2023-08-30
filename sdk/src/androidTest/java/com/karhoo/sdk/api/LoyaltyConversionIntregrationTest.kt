package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.assertj.core.api.Java6Assertions.assertThat
import com.karhoo.sdk.api.model.LoyaltyConversion
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.LOYALTY_CONVERSION
import com.karhoo.sdk.api.util.ServerRobot.Companion.LOYALTY_ID
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.serverRobot
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class LoyaltyConversionIntregrationTest {

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
     * Given:   Loyalty conversion on a payment is requested
     * When:    It is a user booking
     * And:     Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun loyaltyConversionSuccess() {
        serverRobot {
            loyaltyConversionResponse(code = HTTP_OK, response = LOYALTY_CONVERSION)
        }
        var result: LoyaltyConversion? = null

        KarhooApi.loyaltyService.getConversionRates(LOYALTY_ID).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(LOYALTY_CONVERSION)
    }

    /**
     * Given:   Loyalty Conversion is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/

    @Test
    fun invalidJsonWhenRequestingLoyaltyConversion() {
        serverRobot {
            loyaltyConversionResponse(code = HTTP_OK, response = INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.loyaltyService.getConversionRates(LOYALTY_ID).execute {
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
     * Given:   Loyalty Conversion is requested
     * When:    Error 401 but with error payload
     * Then:    An error should be returned
     **/

    @Test
    fun invalidSessionTokenWhenRequestingLoyaltyConversion() {
        serverRobot {
            loyaltyConversionResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }
        var result: KarhooError? = null

        KarhooApi.loyaltyService.getConversionRates(LOYALTY_ID).execute {
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
     * Given:   Loyalty Conversion is requested
     * When:    Success 201 but with no body
     * Then:    An error should be returned
     **/

    @Test
    fun noBodyErrorWhenRequestingLoyaltyConversion() {
        serverRobot {
            loyaltyConversionResponse(code = HTTP_OK, response = NO_BODY)
        }
        var result: KarhooError? = null

        KarhooApi.loyaltyService.getConversionRates(LOYALTY_ID).execute {
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
     * Given:   Loyalty Conversion is requested
     * When:    Error 401 but with empty payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithEmptyBodyWhenRequestingLoyaltyConversion() {
        serverRobot {
            loyaltyConversionResponse(code = HTTP_UNAUTHORIZED, response = EMPTY)
        }
        var result: KarhooError? = null

        KarhooApi.loyaltyService.getConversionRates(LOYALTY_ID).execute {
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
     * Given:   Loyalty Conversion is requested
     * When:    Error 401 but with invalid payload
     * Then:    The karhoo error should be valid
     **/

    @Test
    fun errorResponseWithInvalidJsonyWhenRequestingLoyaltyConversion() {
        serverRobot {
            loyaltyConversionResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }
        var result: KarhooError? = null

        KarhooApi.loyaltyService.getConversionRates(LOYALTY_ID).execute {
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
     * Given:   Loyalty Conversion is requested
     * When:    The response takes too long
     * Then:    The timeout error should be returned
     **/

    @Test
    fun timeoutErrorResponseWhenRequestingLoyaltyConversion() {
        serverRobot {
            loyaltyConversionResponse(code = HTTP_OK, response = INVALID_JSON, delayInMillis = 20000)
        }
        var result: KarhooError? = null

        KarhooApi.loyaltyService.getConversionRates(LOYALTY_ID).execute {
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