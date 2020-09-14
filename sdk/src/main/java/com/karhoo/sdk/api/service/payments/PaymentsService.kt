package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.model.adyen.AdyenPaymentDetails
import com.karhoo.sdk.api.model.adyen.PaymentsDetailsRequestPayload
import com.karhoo.sdk.api.network.request.AddPaymentRequest
import com.karhoo.sdk.api.network.request.NonceRequest
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.call.Call

interface PaymentsService {

    fun initialisePaymentSDK(sdkInitRequest: SDKInitRequest): Call<BraintreeSDKToken>

    fun addPaymentMethod(request: AddPaymentRequest): Call<PaymentsNonce>

    fun getNonce(request: NonceRequest): Call<PaymentsNonce>

    fun getPaymentProvider(): Call<PaymentProvider>

    fun getAdyenPaymentMethods(): Call<String>

    fun getAdyenPayments(request: String): Call<String>

    fun getAdyenPaymentDetails(paymentDetails: PaymentsDetailsRequestPayload): Call<AdyenPaymentDetails>
}
