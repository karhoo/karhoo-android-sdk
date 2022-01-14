package com.karhoo.sdk.api

import junit.framework.Assert.assertEquals
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class KarhooErrorTest {

    /**
     * Given:   A generic throwable is thrown
     * When:    Parsing the exception
     * Then:    An Unexpected error is returned
     */
    @Test
    fun `parses generic throwable as Unexpected`() {
        val internalMessage = "Some internal message for the throwable."

        val errorResult = KarhooError.fromThrowable(Throwable(internalMessage))

        assertEquals(KarhooError.Unexpected, errorResult)
        assertEquals(internalMessage, errorResult.internalMessage)
    }

    /**
     * Given:   A 404 HttpNotFoundException is thrown
     * When:    Parsing the exception
     * Then:    An HttpNotFound error is returned
     */
    @Test
    fun `parses HttpException with 404 code as HttpNotFound`() {
        val exception = HttpException(Response.error<Any>(404, ResponseBody.create(null, "")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(KarhooError.Unexpected, errorResult)
        assertEquals("404", errorResult.code)
    }

    /**
     * Given:   A 500 Http code is thrown
     * When:    Parsing the exception
     * Then:    An HttpNotFound error is returned
     */
    @Test
    fun `a standard http error gets converted to a KarhooError`() {
        val message = "General request error."
        val code = "K0001"
        val exception = HttpException(Response.error<Any>(500,
                ResponseBody.create(null, "{\"code\":\"$code\",\"message\":\"$message\"}")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(message, errorResult.internalMessage)
        assertEquals(code, errorResult.code)
    }


    /**
     * Given:   A 400 Http code is thrown for a loyalty request with an empty-currency slug
     * When:    Parsing the exception
     * Then:    An LoyaltyEmptyCurrency error is resulted
     */
    @Test
    fun `Getting a empty-currency slug, the outputted karhooError will be `() {
        val message = "currency cannot be empty."
        val slug = "empty-currency"
        val exception = HttpException(Response.error<Any>(400,
                                                          ResponseBody.create(null,
                                                                              "{\"slug\":\"$slug\",\"message\":\"$message\"}")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(errorResult, KarhooError.LoyaltyEmptyCurrency)
    }

    /**
     * Given:   A 400 Http code is thrown for a loyalty request with an customer-not-allowed-to-burn-points slug
     * When:    Parsing the exception
     * Then:    An LoyaltyNotAllowedToBurnPoints error is resulted
     */
    @Test
    fun `Getting a customer-not-allowed-to-burn-points slug, the outputted karhooError will be LoyaltyNotAllowedToBurnPoints `
            () {
        val message = "You are not allowed to burn points"
        val slug = "customer-not-allowed-to-burn-points"
        val exception = HttpException(Response.error<Any>(400,
                                                          ResponseBody.create(null,
                                                                              "{\"slug\":\"$slug\",\"message\":\"$message\"}")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(errorResult, KarhooError.LoyaltyNotAllowedToBurnPoints)
    }

    /**
     * Given:   A 400 Http code is thrown for a loyalty request with an incoming-customer-points-exceed-balance slug
     * When:    Parsing the exception
     * Then:    An LoyaltyIncomingPointsExceedBalance error is resulted
     */
    @Test
    fun `Getting a incoming-customer-points-exceed-balance slug, the outputted karhooError will be LoyaltyIncomingPointsExceedBalance `
            () {
        val message = "Your points balance is insufficient"
        val slug = "incoming-customer-points-exceed-balance"
        val exception = HttpException(Response.error<Any>(400,
                                                          ResponseBody.create(null,
                                                                              "{\"slug\":\"$slug\",\"message\":\"$message\"}")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(errorResult, KarhooError.LoyaltyIncomingPointsExceedBalance)
    }

    /**
     * Given:   A 400 Http code is thrown for a loyalty request with an unknown-currency slug
     * When:    Parsing the exception
     * Then:    An LoyaltyUnknownCurrency error is resulted
     */
    @Test
    fun `Getting a incoming-customer-points-exceed-balance slug, the outputted karhooError will be LoyaltyUnknownCurrency `
            () {
        val message = "The currency is not supported yet"
        val slug = "unknown-currency"
        val exception = HttpException(Response.error<Any>(400,
                                                          ResponseBody.create(null,
                                                                              "{\"slug\":\"$slug\",\"message\":\"$message\"}")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(errorResult, KarhooError.LoyaltyUnknownCurrency)
    }

    /**
     * Given:   A 400 Http code is thrown for a loyalty request with an internal-error slug
     * When:    Parsing the exception
     * Then:    An LoyaltyInternalError error is resulted
     */
    @Test
    fun `Getting a incoming-customer-points-exceed-balance slug, the outputted karhooError will be LoyaltyInternalError `
            () {
        val message = "Internal error"
        val slug = "internal-error"
        val exception = HttpException(Response.error<Any>(400,
                                                          ResponseBody.create(null,
                                                                              "{\"slug\":\"$slug\",\"message\":\"$message\"}")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(errorResult, KarhooError.LoyaltyInternalError)
    }

    /**
     * Given:   An unrecognised HTTP error is thrown
     * When:    Parsing the error fails
     * Then:    A default error should be returned
     **/
    @Test
    fun `parsing a http error fails and returns an unexpected error`() {
        val exception = HttpException(Response.error<Any>(500, ResponseBody.create(null, "")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(KarhooError.Unexpected, errorResult)
    }

    /**
     * Given:   A Custom Error happens with custom error code
     * When:    Parsing the error fails
     * Then:    A custom error should be returned
     **/
    @Test
    fun `parsing a custom error happened and returns an custom error`() {
        val errorResult = KarhooError.fromCustomError("customCode", "customInternalMessage",
                                                      "customUserFriendlyMessage")

        assertEquals(KarhooError.Custom, errorResult)
        assertEquals(KarhooError.Custom.code, "KSDK00 customCode")
    }
}
