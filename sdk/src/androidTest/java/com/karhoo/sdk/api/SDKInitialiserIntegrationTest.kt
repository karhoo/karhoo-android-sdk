package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.PAYMENT_TOKEN
import com.karhoo.sdk.api.util.TestData
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
class SDKInitialiserIntegrationTest {

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
     * Given:   SDK Initialiser is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun sdkInitialiserSuccess() {
        serverRobot {
            successfulToken()
            sdkInitResponse(code = HTTP_CREATED, response = PAYMENT_TOKEN, appendedUrl = sdkInitUrl)
        }

        var result: BraintreeSDKToken? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(TestData.PAYMENT_TOKEN)
    }

    /**
     * Given:   SDK Initialiser is requested\
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenRequestingSDKInitialiserReturnsBlankSDKInitToken() {
        serverRobot {
            sdkInitResponse(code = HTTP_CREATED, response = INVALID_DATA, appendedUrl = sdkInitUrl)
        }

        var result: BraintreeSDKToken? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(TestData.BLANK_PAYMENT_TOKEN)
    }

    /**
     * Given:   SDK Initialiser is requested
     * When:    Success 201 but with bad json
     * Then:    An error should be returned
     **/
    @Test
    fun badJsonSuccessReturnsError() {
        serverRobot {
            sdkInitResponse(code = HTTP_CREATED, response = INVALID_JSON, appendedUrl = sdkInitUrl)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
     * Given:   SDK Initialiser is requested
     * When:    Success 204 but with no body
     * Then:    An error should be returned
     **/
    @Test
    fun noBodyReturnsError() {
        serverRobot {
            sdkInitResponse(code = HTTP_CREATED, response = NO_BODY, appendedUrl = sdkInitUrl)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
     * Given:   SDK Initialiser is requested
     * When:    Error 401 with error payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            sdkInitResponse(code = HTTP_BAD_REQUEST, response = GENERAL_ERROR, appendedUrl = sdkInitUrl)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
     * Given:   SDK Initialiser is requested
     * When:    Error 401 with no body payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            sdkInitResponse(code = HTTP_BAD_REQUEST, response = NO_BODY, appendedUrl = sdkInitUrl)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
     * Given:   SDK Initialiser is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            sdkInitResponse(code = HTTP_BAD_REQUEST, response = EMPTY, appendedUrl = sdkInitUrl)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
     * Given:   SDK Initialiser is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            sdkInitResponse(code = HTTP_BAD_REQUEST, response = INVALID_JSON, appendedUrl = sdkInitUrl)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
     * Given:   SDK Initialiser is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            sdkInitResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA, appendedUrl = sdkInitUrl)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
     * Given:   SDK Initialiser is requested
     * When:    The token response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            sdkInitResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA, appendedUrl = sdkInitUrl, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.paymentsService.initialisePaymentSDK(sdkInitRequest).execute {
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
        val org = "FAKE_ORG"
        val currency = "GBP"

        val sdkInitUrl = "?${APITemplate.IDENTIFIER_ORG}=$org&${APITemplate.IDENTIFIER_CURRENCY}=$currency"

        val sdkInitRequest = SDKInitRequest(
                currency = currency,
                organisationId = org)
    }

}