package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.adyen.PaymentMethods
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AdyenPaymentMethodsInteractorTest : BaseKarhooUserInteractorTest() {

    private val adyenPaymentMethods: PaymentMethods = mock()

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
    fun `get Adyen payment methods call failure returns an error`() {
        var shouldBeNull: PaymentMethods? = null
        var error: KarhooError? = null
        var expectedError: KarhooError = KarhooError.InternalSDKError
        whenever(apiTemplate.getPaymentMethods(any()))
                .thenReturn(CompletableDeferred(Resource.Failure(expectedError)))

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
        whenever(apiTemplate.getPaymentMethods(any()))
                .thenReturn(CompletableDeferred(Resource.Success(adyenPaymentMethods)))

        var paymentMethods: PaymentMethods? = null

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> paymentMethods = it.data
                }
            }
            delay(20)
        }

        assertEquals(adyenPaymentMethods, paymentMethods)
    }
}