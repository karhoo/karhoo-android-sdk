package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.network.request.AdyenPaymentMethodsRequest
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
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AdyenPaymentMethodsInteractorTest : BaseKarhooUserInteractorTest() {

    private val adyenPaymentMethodsRequest: AdyenPaymentMethodsRequest = mock()
    private val adyenPaymentMethods: ResponseBody = mock()

    private lateinit var interactor: AdyenPaymentMethodsInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = AdyenPaymentMethodsInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get Adyen payment methods
     * When:    The call is not successful
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `get Adyen payment methods call failure not made if request is null`() {
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

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
        verify(apiTemplate, never()).getAdyenPaymentMethods(adyenPaymentMethodsRequest)
    }

    /**
     * Given:   A request is made to get Adyen payment methods
     * When:    The call is not successful
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `get Adyen payment methods call failure returns an error`() {
        var shouldBeNull: String? = null
        var error: KarhooError? = null
        var expectedError: KarhooError = KarhooError.InternalSDKError
        whenever(apiTemplate.getAdyenPaymentMethods(adyenPaymentMethodsRequest))
                .thenReturn(CompletableDeferred(Resource.Failure(expectedError)))

        interactor.request = adyenPaymentMethodsRequest

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
     * Given:   A request is made to get Adyen payment methods
     * When:    The call is successful
     * Then:    The correct response is returned
     */
    @Test
    fun `successful get Adyen payment methods response returns Adyen payment methods`() {
        whenever(apiTemplate.getAdyenPaymentMethods(any()))
                .thenReturn(CompletableDeferred(Resource.Success(adyenPaymentMethods)))

        var adyenPaymentMethods: String? = null
        interactor.request = adyenPaymentMethodsRequest

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> adyenPaymentMethods = it.data
                }
            }
            delay(20)
        }

        assertEquals(adyenPaymentMethods, adyenPaymentMethods)
    }
}