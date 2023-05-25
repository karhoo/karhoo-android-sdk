package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooApi
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.adyen.AdyenClientKey
import com.karhoo.sdk.api.model.adyen.AdyenPublicKey
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.AdyenPaymentMethodsRequest
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.call.Call
import org.json.JSONObject
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

    override fun getPaymentProvider(): Call<PaymentProvider> = PaymentProviderInteractor(credentialsManager, userManager, apiTemplate, KarhooApi.paymentsService).apply {}

    @Deprecated("Use the client-key instead", ReplaceWith(
        "AdyenClientKeyInteractor(credentialsManager, apiTemplate).apply {}",
        "com.karhoo.sdk.api.service.payments.AdyenClientKeyInteractor"
    )
    )
    override fun getAdyenPublicKey(): Call<AdyenPublicKey> = AdyenPublicKeyInteractor(credentialsManager, apiTemplate).apply {}

    override fun getAdyenPaymentMethods(request: AdyenPaymentMethodsRequest): Call<String> =
            AdyenPaymentMethodsInteractor(credentialsManager, apiTemplate).apply {
                userManager.paymentProvider?.provider?.version?.let { this.version = it }
                this.adyenPaymentMethodsRequest = request
            }

    override fun getAdyenPayments(request: String): Call<JSONObject> =
            AdyenPaymentsInteractor(credentialsManager, apiTemplate).apply {
                userManager.paymentProvider?.provider?.version?.let { this.version = it }
                this.adyenPaymentsRequest = request
            }

    override fun getAdyenPaymentDetails(paymentDetails: String): Call<JSONObject> = AdyenPaymentsDetailsInteractor(credentialsManager, apiTemplate).apply {
        userManager.paymentProvider?.provider?.version?.let { this.version = it }
        this.adyenPaymentsDetails = paymentDetails
    }

    override fun getAdyenClientKey(): Call<AdyenClientKey> = AdyenClientKeyInteractor(credentialsManager, apiTemplate).apply {}

}
