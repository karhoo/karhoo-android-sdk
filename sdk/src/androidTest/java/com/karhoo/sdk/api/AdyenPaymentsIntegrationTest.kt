package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.adyen.AdyenAmount
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsRequestPayload
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsResponse
import com.karhoo.sdk.api.model.adyen.AdyenStoredPaymentMethod
import com.karhoo.sdk.api.network.request.AdyenPaymentsRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.ADYEN_PAYMENTS
import com.karhoo.sdk.api.util.ServerRobot.Companion.ADYEN_PAYMENT_METHODS
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
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

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class AdyenPaymentsIntegrationTest {

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
     * Given:   Adyen payments are retrieved
     * When:    A successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun getAdyenPaymentsSuccess() {
        serverRobot {
            getAdyenPaymentsResponse(HTTP_CREATED, ADYEN_PAYMENTS)
        }

        var result: AdyenPaymentsResponse? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(5, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Adyen payments are retrieved
     * When:    The call is successful but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenGettingAdyenPaymentsReturnsInternalError() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(200, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Adyen payments are retrieved
     * When:    The call is successful but with bad json
     * Then:    A blank object should be returned
     **/
    @Test
    fun badJsonSuccessReturnsBlankResult() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_CREATED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
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
     * Given:   Adyen payments are retrieved
     * When:    The call is successful but with no body
     * Then:    A blank object should be returned
     **/
    @Test
    fun blankBodyReturnsDefaultObject() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_CREATED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   Adyen payments are retrieved
     * When:    An error is returned with error payload
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_BAD_REQUEST, response = GENERAL_ERROR)
        }

        var expected = KarhooError.GeneralRequestError
        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(expected)
    }

    /**
     * Given:   Adyen payments are retrieved
     * When:    An error is returned with no body payload
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_BAD_REQUEST, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
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
     * Given:   Adyen payments are retrieved
     * When:    An error is returned with an empty body
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_BAD_REQUEST, response = EMPTY)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
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
     * Given:   Adyen payments are retrieved
     * When:    An error is returned with invalid json
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_BAD_REQUEST, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
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
     * Given:   Adyen payments are retrieved
     * When:    An error is returned for invalid data
     * Then:    The Karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
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
     * Given:   Adyen payments are retrieved
     * When:    The response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            getAdyenPaymentsResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.getAdyenPayments(request).execute {
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

    companion object {
        val request: AdyenPaymentsRequest = AdyenPaymentsRequest(
                paymentsPayload = AdyenPaymentsRequestPayload(
                        amount = AdyenAmount(currency = "GBP", value = 100.00),
                        merchantAccount = "AB123",
                        storedPaymentMethod = AdyenStoredPaymentMethod(),
                        reference = "12345",
                        returnUrl = "http://google.com"),
                returnUrlSuffix = "return_url_suffix"
                                                                )
    }
}
