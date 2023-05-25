package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.adyen.AdyenClientKey
import com.karhoo.sdk.api.model.adyen.AdyenPublicKey
import com.karhoo.sdk.api.network.request.AdyenPaymentMethodsRequest
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.call.Call
import org.json.JSONObject

interface PaymentsService {

    fun initialisePaymentSDK(sdkInitRequest: SDKInitRequest): Call<BraintreeSDKToken>

    fun getPaymentProvider(): Call<PaymentProvider>

    fun getAdyenPublicKey(): Call<AdyenPublicKey>

    fun getAdyenPaymentMethods(request: AdyenPaymentMethodsRequest): Call<String>

    fun getAdyenPayments(request: String): Call<JSONObject>

    fun getAdyenPaymentDetails(paymentDetails: String): Call<JSONObject>

    fun getAdyenClientKey(): Call<AdyenClientKey>
}
