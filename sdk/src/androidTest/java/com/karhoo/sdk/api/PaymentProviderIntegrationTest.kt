package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.PAYMENT_PROVIDER
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_CREATED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PaymentProviderIntegrationTest {
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
     * Given:   Payment provider methods are retrieved
     * When:    A successful response is returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun getPaymentProviderSuccess() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_CREATED, response = PAYMENT_PROVIDER)
        }

        var result: PaymentProvider? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(5, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Payment Provider methods are retrieved
     * When:    The call is successful but with invalid data
     * Then:    An internal SDK error should be returned
     **/
    @Test
    fun invalidDataWhenGettingPaymentProviderMethodsReturnsInternalError() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(200, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Payment Provider methods are retrieved
     * When:    The call is successful but with bad JSON
     * Then:    A blank object should be returned
     **/
    @Test
    fun badJsonSuccessReturnsBlankResultPaymentProvider() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_CREATED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
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
     * Given:   Payment Provider methods are retrieved
     * When:    The call is successful but with no body
     * Then:    A blank object should be returned
     **/
    @Test
    fun blankBodyReturnsDefaultObjectPaymentProvider() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_CREATED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Payment Provider methods are retrieved
     * When:    An error is returned with error payload
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooErrorPaymentProvider() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_BAD_REQUEST, response = GENERAL_ERROR)
        }

        var expected = KarhooError.GeneralRequestError
        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(expected)
    }

    /**
     * Given:   Payment Provider methods are retrieved
     * When:    An error is returned with an empty body
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooErrorPaymentProvider() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_BAD_REQUEST, response = EMPTY)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
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
     * Given:   Payment Provider methods are retrieved
     * When:    An error is returned with an invalid Json
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooErrorPaymentProvider() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_BAD_REQUEST, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
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
     * Given:   Payment Provider methods are retrieved
     * When:    An error is returned with invaid data
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooErrorPaymentProvider() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
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
     * Given:   Payment Provider methods are retrieved
     * When:    The response takes too long
     * Then:    The Timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLongPaymentProvider() {
        serverRobot {
            getPaymentProviderMethodsResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA,
                                              delayInMillis = 2000)
        }
        var result: KarhooError? = null

        KarhooApi.paymentsService.getPaymentProvider().execute {
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
