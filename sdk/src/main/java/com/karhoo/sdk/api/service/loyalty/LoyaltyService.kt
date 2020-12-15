package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.call.Call

interface LoyaltyService {

    fun getBalance(loyaltyID: String): Call<LoyaltyBalance>
}