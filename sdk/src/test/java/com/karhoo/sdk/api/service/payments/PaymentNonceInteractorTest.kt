package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.CardType
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.network.request.AddPaymentRequest
import com.karhoo.sdk.api.network.request.Payer
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PaymentNonceInteractorTest : BaseKarhooUserInteractorTest() {

    private val userManager: UserManager = mock()

    private lateinit var interactor: PaymentNonceInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = PaymentNonceInteractor(credentialsManager, apiTemplate, userManager, context)
    }

    /**
     * Given:   A request is made to set up payment method
     * When:    The request is executed
     * Then:    The values should be sent to the BE
     *
     */
    @Test
    fun `creating Payment Method Sends Values To BE`() {
        val payer = Payer(id = "id", firstName = "first_name", lastName = "last_name", email = "email@domain.com")
        val request = AddPaymentRequest(payer, "org_id", "nonce")
        whenever(apiTemplate.addPayment(request))
                .thenReturn(CompletableDeferred(Resource.Success(paymentsNonceExpected)))

        var paymentsNonce: PaymentsNonce? = null

        interactor.request = request
        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> paymentsNonce = it.data
                }
            }
            delay(20)
        }

        assertEquals(paymentsNonce!!, paymentsNonceExpected)
    }

    /**
     * Given:   The nonce value isn't set
     * When:    A request is made to add a users nonce
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `add payment method when nonce not set returns an error`() {
        var shouldBeNull: PaymentsNonce? = null
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
        val paymentsNonceExpected = PaymentsNonce("returnednonce", CardType.VISA)
    }
}