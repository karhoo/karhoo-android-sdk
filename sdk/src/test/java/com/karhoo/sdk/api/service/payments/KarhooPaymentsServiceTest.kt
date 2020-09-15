package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AddPaymentRequest
import com.karhoo.sdk.api.network.request.AdyenPaymentMethodsRequest
import com.karhoo.sdk.api.network.request.NonceRequest
import com.karhoo.sdk.api.network.request.Payer
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooPaymentsServiceTest {

    // Needed for InjectMocks
    private val userManager: UserManager = mock()
    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()

    @InjectMocks
    private lateinit var service: KarhooPaymentsService

    /**
     * Given:   A request is made to get a call on SDK Initialise
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun createSDKInitCallWhenGettingSDKToken() {
        val call = service.initialisePaymentSDK(SDKInitRequest("org_id", "nonce"))
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a call on payment method
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun createPaymentMethodCallWhenSettingPaymentMethod() {
        val payer = Payer(id = "id", firstName = "first_name", lastName = "last_name", email = "email@domain.com")
        val addPaymentRequest = AddPaymentRequest(payer, "org_id", "nonce")
        val call = service.addPaymentMethod(addPaymentRequest)
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a nonce for a payment method
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun getNonceCallWhenGettingPaymentMethod() {
        val payer = Payer(id = "id", firstName = "first_name", lastName = "last_name", email = "email@domain.com")
        val addPaymentRequest = NonceRequest(payer, "org_id")
        val call = service.getNonce(addPaymentRequest)
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get payment provider methods
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun getPaymentProviderMethodsCallWhenGettingPaymentProviderMethods() {
        val call = service.getPaymentProvider()
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get Adyen payment methods
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun getAdyenPaymentMethodsCallWhenGettingAdyenPaymentMethods() {
        val call = service.getAdyenPaymentMethods(AdyenPaymentMethodsRequest())
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get Adyen payments
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun getAdyenPaymentsCallWhenGettingAdyenPayments() {
        val call = service.getAdyenPayments("request")
        assertNotNull(call)
    }

    /**
     * Given: A request is made to get Adyen payment details
     * When: The call is constructed and executed
     * Then: A call should be made to the appropriate endpoint
     */
    @Test
    fun getAdyenPaymentsDetailsCallWhenGettingAdyenPaymentsDetails() {
        val call = service.getAdyenPaymentDetails("request")
        assertNotNull(call)
    }
}