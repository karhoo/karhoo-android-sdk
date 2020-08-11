package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PaymentProviderInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: PaymentProviderInteractor

    private val providerId = PaymentProvider(
            provider = "provider_1234",
            loyalty = emptyList()
                                            )

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = PaymentProviderInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given: No payment provider is chosen
     * When: A request is made to check payment provider
     * Then: An error should be returned stating provider not set
     **/
    @Test
    fun `unsuccessful selection of payment provider returns an error`() {
        var shouldBeNull: PaymentProvider? = null
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
}