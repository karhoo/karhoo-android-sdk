package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.api.model.LoyaltyPoints
import com.karhoo.sdk.api.model.LoyaltyConversion
import com.karhoo.sdk.api.model.LoyaltyNonce
import com.karhoo.sdk.api.model.LoyaltyStatus
import com.karhoo.sdk.api.network.request.LoyaltyPreAuthPayload
import com.karhoo.sdk.call.Call

interface LoyaltyService {

    fun getBalance(loyaltyID: String): Call<LoyaltyBalance>

    fun getConversionRates(loyaltyID: String): Call<LoyaltyConversion>

    fun getLoyaltyStatus(loyaltyID: String): Call<LoyaltyStatus>

    fun getLoyaltyBurn(loyaltyID: String, currency: String, cents: Int):
            Call<LoyaltyPoints>

    fun getLoyaltyEarn(loyaltyID: String, currency: String, totalAmount: Int, burnPoints: Int):
            Call<LoyaltyPoints>

    fun getLoyaltyPreAuth(loyaltyID: String, request: LoyaltyPreAuthPayload): Call<LoyaltyNonce>
}
