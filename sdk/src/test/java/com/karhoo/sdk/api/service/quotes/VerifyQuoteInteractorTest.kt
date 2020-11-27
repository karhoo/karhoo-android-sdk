package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.FleetInfo
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuotePrice
import com.karhoo.sdk.api.model.QuoteSource
import com.karhoo.sdk.api.model.QuoteType
import com.karhoo.sdk.api.model.QuoteVehicle
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VerifyQuoteInteractorTest : BaseKarhooUserInteractorTest() {

    internal lateinit var interactor: VerifyQuoteInteractor

    private val id = "129e51a-bc10-11e8-a821-0a580a0414db"

    private val quoteRequest = QuoteId(id)

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = VerifyQuoteInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to check fleet coverage of an area
     * When:    There is coverage in the area
     * Then:    A successful response is returned
     **/
    @Test
    fun checkVerifyQuoteSuccessResponse() {
        val quoteInfo = Quote(id, PRICE, null, QUOTETYPE, QUOTESOURCE, FLEET, VEHICLE)
        whenever(apiTemplate.verifyQuotes(id))
                .thenReturn(CompletableDeferred(Resource.Success(quoteInfo)))
        interactor.quoteIdRequest = quoteRequest

        var returnedVerifyInfo: Quote? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedVerifyInfo = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }
        assertNotNull(returnedVerifyInfo)
        assertEquals(quoteInfo, returnedVerifyInfo)
        verify(apiTemplate).verifyQuotes(id)
    }

    /**
     * Given:   A request is made to check fleet coverage of an area
     * When:    There is no coverage in the area
     * Then:    A failure response is returned
     **/
    @Test
    fun checkVerifyQuoteFailureResponse() {
        val quoteInfo = Quote(id, PRICE, null, QUOTETYPE, QUOTESOURCE, FLEET, VEHICLE)
        whenever(apiTemplate.verifyQuotes(id))
                .thenReturn(CompletableDeferred(Resource.Success(quoteInfo)))
        interactor.quoteIdRequest = quoteRequest

        var returnedVerifyInfo: Quote? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedVerifyInfo = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }
        assertNotNull(returnedVerifyInfo)
        assertEquals(quoteInfo, returnedVerifyInfo)
        verify(apiTemplate).verifyQuotes(id)
    }

    /**
     * Given:   Id is not set
     * When:    A request is made to verify quote
     * Then:    An Internal SDK error is returned
     **/
    @Test
    fun verifyQuoteReturnsAnErrorIfNoIdIsGiven() {
        var shouldBeNull: Quote? = null
        var error: KarhooError? = null

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
    }

    companion object {
        val QUOTETYPE = QuoteType.ESTIMATED

        val QUOTESOURCE = QuoteSource.FLEET

        val PRICE = QuotePrice()

        val FLEET = FleetInfo()

        val VEHICLE = QuoteVehicle()
    }
}