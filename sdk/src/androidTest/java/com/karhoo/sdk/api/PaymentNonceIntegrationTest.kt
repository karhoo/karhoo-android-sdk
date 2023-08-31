//package com.karhoo.sdk.api
//
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.github.tomakehurst.wiremock.junit.WireMockRule
//import com.karhoo.sdk.api.network.request.AddPaymentRequest
//import com.karhoo.sdk.api.network.request.Payer
//import com.karhoo.sdk.api.network.response.Resource
//import com.karhoo.sdk.api.testrunner.SDKTestConfig
//import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
//import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
//import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
//import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
//import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
//import com.karhoo.sdk.api.util.ServerRobot.Companion.PAYMENTS_TOKEN
//import com.karhoo.sdk.api.util.serverRobot
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.net.HttpURLConnection.HTTP_BAD_REQUEST
//import java.net.HttpURLConnection.HTTP_CREATED
//import java.util.concurrent.CountDownLatch
//import java.util.concurrent.TimeUnit
//
///**
// * Instrumented test, which will execute on an Android device.
// *
// * @see [Testing documentation](http://d.android.com/tools/testing)
// */
//@RunWith(AndroidJUnit4::class)
//class PaymentNonceIntegrationTest {
//
//    @get:Rule
//    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)
//
//    private val latch = CountDownLatch(1)
//
//    @Before
//    fun setUp() {
//        serverRobot {
//            successfulToken()
//        }
//    }
//
//    @After
//    fun tearDown() {
//        wireMockRule.resetAll()
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Successful response has been returned
//     * Then:    The response payload should be valid
//     **/
//    @Test
//    fun addCardSuccess() {
//        serverRobot {
//            addCardResponse(HTTP_CREATED, PAYMENTS_TOKEN)
//        }
//
////        var result: PaymentsNonce? = null
////
////        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
////            when (it) {
////                is Resource.Success -> {
////                    result = it.data
////                    latch.countDown()
////                }
////            }
////        }
////
////        latch.await(5, TimeUnit.SECONDS)
////        assertThat(result).isNotNull
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Success 204 but with invalid data
//     * Then:    An internal sdk error should be returned
//     **/
//    @Test
//    fun invalidDataWhenAddingCardReturnsInternalError() {
//        serverRobot {
//            addCardResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(200, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(KarhooError.Unexpected)
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Success 204 but with bad json
//     * Then:    A blank object should be returned
//     **/
//    @Test
//    fun badJsonSuccessReturnsBlankResult() {
//        serverRobot {
//            addCardResponse(code = HTTP_CREATED, response = INVALID_JSON)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(KarhooError.Unexpected)
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Success 204 but with no body
//     * Then:    A blank object should be returned
//     **/
//    @Test
//    fun blankBodyReturnsDefaultObject() {
//        serverRobot {
//            addCardResponse(code = HTTP_CREATED, response = NO_BODY)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isNotNull
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Error 401 with error payload
//     * Then:    The karhoo error should be valid
//     **/
//    @Test
//    fun errorResponseGetsParsedIntoKarhooError() {
//        serverRobot {
//            addCardResponse(code = HTTP_BAD_REQUEST, response = GENERAL_ERROR)
//        }
//
//        var expected = KarhooError.GeneralRequestError
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(expected)
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Error 401 with no body payload
//     * Then:    The karhoo error should be valid
//     **/
//    @Test
//    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
//        serverRobot {
//            addCardResponse(code = HTTP_BAD_REQUEST, response = NO_BODY)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(KarhooError.Unexpected)
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Error 401 with empty payload
//     * Then:    The karhoo error should be valid
//     **/
//    @Test
//    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
//        serverRobot {
//            addCardResponse(code = HTTP_BAD_REQUEST, response = EMPTY)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(KarhooError.Unexpected)
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Error 401 with empty payload
//     * Then:    The karhoo error should be valid
//     **/
//    @Test
//    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
//        serverRobot {
//            addCardResponse(code = HTTP_BAD_REQUEST, response = INVALID_JSON)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(KarhooError.Unexpected)
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    Error 401 with empty payload
//     * Then:    The karhoo error should be valid
//     **/
//    @Test
//    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
//        serverRobot {
//            addCardResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(KarhooError.Unexpected)
//    }
//
//    /**
//     * Given:   A card has been added
//     * When:    The add card response takes too long
//     * Then:    The timeout error should be returned
//     **/
//    @Test
//    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
//        serverRobot {
//            addCardResponse(code = HTTP_BAD_REQUEST, response = INVALID_DATA, delayInMillis = 2000)
//        }
//
//        var result: KarhooError? = null
//
//        KarhooApi.paymentsService.addPaymentMethod(AddPaymentRequest(payer, org, nonce)).execute {
//            when (it) {
//                is Resource.Failure -> {
//                    result = it.error
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await(2, TimeUnit.SECONDS)
//        assertThat(result).isEqualTo(KarhooError.Timeout)
//    }
//
//    companion object {
//        val payer = Payer(
//                id = "123",
//                email = "name@email.com",
//                firstName = "John",
//                lastName = "Smith")
//
//        val org = "FAKE_ORG"
//
//        val nonce = "Test Nonce"
//    }
//
//}
//
