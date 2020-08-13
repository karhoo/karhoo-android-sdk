package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.model.adyen.PaymentMethods
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AddPaymentRequest
import com.karhoo.sdk.api.network.request.AdyenPaymentMethodsRequest
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

    override fun getAdyenPaymentMethods(): Call<PaymentMethods> =
            AdyenPaymentMethodsInteractor(credentialsManager, apiTemplate).apply {}

}
