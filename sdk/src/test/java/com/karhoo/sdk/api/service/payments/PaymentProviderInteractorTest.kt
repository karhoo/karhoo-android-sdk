package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.model.Provider
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.karhoo.sdk.call.Call
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PaymentProviderInteractorTest : BaseKarhooUserInteractorTest() {

    private var organisation: Organisation = mock()
    private var paymentService: PaymentsService = mock()
    private var userManager: UserManager = mock()
    private var userInfo: UserInfo = mock()

    private val adyenProvider = PaymentProvider(Provider(id = "Adyen"))
    private val braintreeProvider = PaymentProvider(Provider(id = "Braintree"))

    private var paymentsCall: Call<PaymentsNonce> = mock()

    private lateinit var interactor: PaymentProviderInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = PaymentProviderInteractor(credentialsManager, userManager, apiTemplate,
                                               paymentService, context)
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
        val expectedError: KarhooError = KarhooError.InternalSDKError


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

        assertEquals(expectedError, error)
        assertNull(shouldBeNull)
    }

    /**
     * Given:   A request is made to get payment provider methods
     * When:    The call is successful
     * And:     The user is a Braintree user
     * Then:    The correct response is returned
     * And:     The nonce is retrieved
     **/
    @Test
    fun `successful selection of payment provider returns Payment provider methods`() {
        whenever(apiTemplate.getPaymentProvider())
                .thenReturn(CompletableDeferred(Resource.Success(braintreeProvider)))
        whenever(paymentService.getNonce(any())).thenReturn(paymentsCall)
        whenever(userManager.user).thenReturn(userInfo)
        whenever(userInfo.userId).thenReturn("1234")
        whenever(userInfo.firstName).thenReturn("Joe")
        whenever(userInfo.lastName).thenReturn("Bloggs")
        whenever(userInfo.email).thenReturn("test@test.test")
        whenever(userInfo.organisations).thenReturn(listOf(organisation))
        whenever(organisation.id).thenReturn("1111")

        var paymentProvider: PaymentProvider? = null

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> {
                        verify(userManager).paymentProvider = it.data.provider
                        verify(paymentService).getNonce(any())
                        paymentProvider = it.data
                    }
                }
            }
            delay(20)
        }

        assertEquals(braintreeProvider, paymentProvider)
    }

    /**
     * Given:   A request is made to get payment provider methods
     * When:    The call is successful
     * And:     The user is an Adyen user
     * Then:    A call is made to get the nonce
     **/
    @Test
    fun `no nonce call made for Adyen users`() {
        whenever(apiTemplate.getPaymentProvider())
                .thenReturn(CompletableDeferred(Resource.Success(adyenProvider)))

        var paymentProvider: PaymentProvider? = null

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> {
                        verify(userManager).paymentProvider = it.data.provider
                        verify(paymentService, never()).getNonce(any())
                        paymentProvider = it.data
                    }
                }
            }
            delay(20)
        }

        assertEquals(adyenProvider, paymentProvider)
    }

    /**
     * Given:   A request is made to get payment provider methods
     * When:    The call is successful
     * And:     The user is a guest Braintree user
     * Then:    A call is made to get the nonce
     **/
    @Test
    fun `no nonce call made for guest Braintree users`() {
        whenever(apiTemplate.getPaymentProvider())
                .thenReturn(CompletableDeferred(Resource.Success(braintreeProvider)))
        whenever(userManager.user).thenReturn(userInfo)
        whenever(userInfo.organisations).thenReturn(emptyList())

        var paymentProvider: PaymentProvider? = null

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> {
                        verify(userManager).paymentProvider = it.data.provider
                        verify(paymentService, never()).getNonce(any())
                        paymentProvider = it.data
                    }
                }
            }
            delay(20)
        }

        assertEquals(braintreeProvider, paymentProvider)
    }
}
