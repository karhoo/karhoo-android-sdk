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
     * Given:   An unrecognised HTTP error is thrown
     * When:    Parsing the error fails
     * Then:    A defult error should be returned
     **/
    @Test
    fun `parsing a http error fails and returns an unexpected error`() {
        val exception = HttpException(Response.error<Any>(500, ResponseBody.create(null, "")))

        val errorResult = KarhooError.fromThrowable(exception)

        assertEquals(KarhooError.Unexpected, errorResult)
    }
}