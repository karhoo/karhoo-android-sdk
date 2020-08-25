package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.model.adyen.AdyenPaymentMethods
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AddPaymentRequest
import com.karhoo.sdk.api.network.request.NonceRequest
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooPaymentsService : PaymentsService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    @Inject
    internal lateinit var userManager: UserManager

    override fun initialisePaymentSDK(sdkInitRequest: SDKInitRequest): Call<BraintreeSDKToken> = SDKInitInteractor(credentialsManager, apiTemplate).apply {
        this.sdkInitRequest = sdkInitRequest
    }

    override fun addPaymentMethod(request: AddPaymentRequest): Call<PaymentsNonce> = PaymentNonceInteractor(credentialsManager, apiTemplate, userManager).apply {
        this.request = request
    }

    override fun getNonce(request: NonceRequest): Call<PaymentsNonce> = GetNonceInteractor(credentialsManager, apiTemplate, userManager).apply {
        this.nonceRequest = request
    }

    override fun getPaymentProvider(): Call<PaymentProvider> = PaymentProviderInteractor(credentialsManager, userManager, apiTemplate).apply {}

    override fun getAdyenPaymentMethods(): Call<AdyenPaymentMethods> =
            AdyenPaymentMethodsInteractor(credentialsManager, apiTemplate).apply {}

}
