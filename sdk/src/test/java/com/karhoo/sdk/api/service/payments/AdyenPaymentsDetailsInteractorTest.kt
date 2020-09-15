package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AdyenPaymentsDetailsInteractorTest : BaseKarhooUserInteractorTest() {

    private val adyenPaymentsDetails: String = "{\"field1\":\"some text\", \"field2\":\"some more text\"}"
    private val adyenPaymentsResponse: ResponseBody = mock()

    private lateinit var interactor: AdyenPaymentsDetailsInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = AdyenPaymentsDetailsInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get Adyen payments details
     * When:    The call is not successful
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `get Adyen payments details call failure not made if request is null`() {
        var shouldBeNull: String? = null
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

        Assert.assertEquals(KarhooError.InternalSDKError, error)
        Assert.assertNull(shouldBeNull)
        verify(apiTemplate, never()).getAdyenPaymentDetails(adyenPaymentsDetails)
    }

    /**
     * Given:   A request is made to get Adyen payments details
     * When:    The call is not successful
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `get Adyen payments details call failure returns an error`() {
        var shouldBeNull: String? = null
        var error: KarhooError? = null
        whenever(apiTemplate.getAdyenPaymentDetails(adyenPaymentsDetails))
                .thenReturn(CompletableDeferred(Resource.Failure(KarhooError.InternalSDKError)))

        interactor.adyenPaymentsDetails = adyenPaymentsDetails

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        Assert.assertEquals(KarhooError.InternalSDKError, error)
        Assert.assertNull(shouldBeNull)
    }

    /**
     * Given:   A request is made to get Adyen payments details
     * When:    The call is successful
     * Then:    The correct response is returned
     */
    @Test
    fun `successful get Adyen payments details response returns Adyen payments details`() {
        whenever(apiTemplate.getAdyenPaymentDetails(any()))
                .thenReturn(CompletableDeferred(Resource.Success(adyenPaymentsResponse)))

        var adyenPaymentsResponse: String? = null

        interactor.adyenPaymentsDetails = adyenPaymentsDetails

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> adyenPaymentsResponse = it.data
                }
            }
            delay(20)
        }

        Assert.assertEquals(adyenPaymentsResponse, adyenPaymentsResponse)
    }
}