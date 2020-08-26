package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsResponse
import com.karhoo.sdk.api.network.request.AdyenPaymentsRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AdyenPaymentsInteractorTest : BaseKarhooUserInteractorTest() {

    private val adyenPaymentsRequest: AdyenPaymentsRequest = mock()
    private val adyenPaymentsResponse: AdyenPaymentsResponse = mock()

    private lateinit var interactor: AdyenPaymentsInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = AdyenPaymentsInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get Adyen payments
     * When:    The call is not successful
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `get Adyen payments call failure not made if request is null`() {
        var shouldBeNull: AdyenPaymentsResponse? = null
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
        verify(apiTemplate, never()).getAdyenPayments(adyenPaymentsRequest)
    }

    /**
     * Given:   A request is made to get Adyen payments
     * When:    The call is not successful
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `get Adyen payments call failure returns an error`() {
        var shouldBeNull: AdyenPaymentsResponse? = null
        var error: KarhooError? = null
        whenever(apiTemplate.getAdyenPayments(adyenPaymentsRequest))
                .thenReturn(CompletableDeferred(Resource.Failure(KarhooError.InternalSDKError)))

        interactor.adyenPaymentsRequest = adyenPaymentsRequest

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

    /**
     * Given:   A request is made to get Adyen payments
     * When:    The call is successful
     * Then:    The correct response is returned
     */
    @Test
    fun `successful get Adyen payments response returns Adyen payments`() {
        whenever(apiTemplate.getAdyenPayments(any()))
                .thenReturn(CompletableDeferred(Resource.Success(adyenPaymentsResponse)))

        var adyenPaymentsResponse: AdyenPaymentsResponse? = null

        interactor.adyenPaymentsRequest = adyenPaymentsRequest

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> adyenPaymentsResponse = it.data
                }
            }
            delay(20)
        }

        assertEquals(adyenPaymentsResponse, adyenPaymentsResponse)
    }
}