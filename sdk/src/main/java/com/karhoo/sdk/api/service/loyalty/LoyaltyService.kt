package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.api.model.LoyaltyBurnPoints
import com.karhoo.sdk.api.model.LoyaltyConversion
import com.karhoo.sdk.api.model.LoyaltyPointsToEarn
import com.karhoo.sdk.api.model.LoyaltyStatus
import com.karhoo.sdk.call.Call
import org.json.JSONObject

interface LoyaltyService {

    fun getBalance(loyaltyID: String): Call<LoyaltyBalance>

    fun getConversionRates(loyaltyID: String): Call<LoyaltyConversion>

    fun getStatus(loyaltyID: String): Call<LoyaltyStatus>

    fun getBurnPointsFromCents(loyaltyID: String, currency: String, cents: Int):
            Call<LoyaltyBurnPoints>

    fun getPointsToEarn(loyaltyID: String, currency: String, totalAmount: Int, burnPoints: Int):
            Call<LoyaltyPointsToEarn>

    fun preAuthLoyaltyPoints(request: String): Call<JSONObject>
}
