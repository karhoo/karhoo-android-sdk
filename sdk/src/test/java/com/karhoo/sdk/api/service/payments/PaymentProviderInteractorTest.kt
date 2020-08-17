package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PaymentProviderInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: PaymentProviderInteractor

    private val providerId = PaymentProvider(
            id = "provider_1234",
            loyalty = emptyList()
                                            )

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = PaymentProviderInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given: A request is made to get payment provider methods
     * When: the call fails
     * Then: An internalSDKError is returned
     **/
    @Test
    fun `unsuccessful selection of payment provider returns an error`() {
        var shouldBeNull: PaymentProvider? = null
        var error: KarhooError? = null
        var expectedError: KarhooError = KarhooError.InternalSDKError


        whenever(apiTemplate.getPaymentProvider())
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
     * Given: A request is made to get payment provider methods
     * When: the call is successful
     * Then: The correct response is returned
     **/
    @Test
    fun `successful selection of payment provider returns Payment provider methods`() {
        whenever(apiTemplate.getPaymentProvider())
                .thenReturn(CompletableDeferred(Resource.Success(providerId)))

        var paymentProvider: PaymentProvider? = null

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> paymentProvider = it.data
                }
            }
            delay(20)
        }

        assertEquals(providerId, paymentProvider)
    }
}