package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.api.model.LoyaltyPoints
import com.karhoo.sdk.api.model.LoyaltyConversion
import com.karhoo.sdk.api.model.LoyaltyNonce
import com.karhoo.sdk.api.model.LoyaltyStatus
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LoyaltyPreAuthPayload
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooLoyaltyService : LoyaltyService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun getBalance(loyaltyID: String): Call<LoyaltyBalance> = LoyaltyBalanceInteractor(credentialsManager, apiTemplate).apply {
        this.loyaltyId = loyaltyID
    }

    override fun getConversionRates(loyaltyID: String): Call<LoyaltyConversion> = LoyaltyConversionInteractor(credentialsManager, apiTemplate).apply {
        this.loyaltyId = loyaltyID
    }

    override fun getLoyaltyStatus(loyaltyID: String): Call<LoyaltyStatus> = LoyaltyStatusInteractor(credentialsManager, apiTemplate).apply {
        this.loyaltyId = loyaltyID
    }

    override fun getLoyaltyBurn(
        loyaltyID: String,
        currency: String,
        cents: Int): Call<LoyaltyPoints>  = LoyaltyPointsInteractor(credentialsManager, apiTemplate).apply {
        this.loyaltyId = loyaltyID
        this.currency = currency
        this.amount = cents
        this.toEarn = false
    }

    override fun getLoyaltyEarn(
        loyaltyID: String,
        currency: String,
        totalAmount: Int,
        burnPoints: Int
                               ): Call<LoyaltyPoints> = LoyaltyPointsInteractor(credentialsManager, apiTemplate).apply {
        this.loyaltyId = loyaltyID
        this.currency = currency
        this.totalAmount = totalAmount
        this.burnPoints = burnPoints
        this.toEarn = true
    }

    override fun getLoyaltyPreAuth(loyaltyID: String, request: LoyaltyPreAuthPayload): Call<LoyaltyNonce> =
        LoyaltyPreAuthInteractor(credentialsManager, apiTemplate).apply {
            this.loyaltyId = loyaltyID
            this.preAuthRequest = request
        }
}
