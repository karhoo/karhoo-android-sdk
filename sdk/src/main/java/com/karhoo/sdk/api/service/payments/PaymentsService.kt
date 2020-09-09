package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.model.adyen.AdyenPaymentMethods
import com.karhoo.sdk.api.model.adyen.AdyenPaymentsResponse
import com.karhoo.sdk.api.network.request.AddPaymentRequest
import com.karhoo.sdk.api.network.request.AdyenPaymentsRequest
import com.karhoo.sdk.api.network.request.NonceRequest
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.call.Call

interface PaymentsService {

    fun initialisePaymentSDK(sdkInitRequest: SDKInitRequest): Call<BraintreeSDKToken>

    fun addPaymentMethod(request: AddPaymentRequest): Call<PaymentsNonce>

    fun getNonce(request: NonceRequest): Call<PaymentsNonce>

    fun getPaymentProvider(): Call<PaymentProvider>

    fun getAdyenPaymentMethods(): Call<AdyenPaymentMethods>

    fun getAdyenPayments(request: AdyenPaymentsRequest): Call<AdyenPaymentsResponse>
}
