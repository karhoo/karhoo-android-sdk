package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.model.adyen.PaymentsDetailsRequestPayload

interface AdyenPaymentsDetailsInteractor {
    fun set(paymentDetails: PaymentsDetailsRequestPayload)
}