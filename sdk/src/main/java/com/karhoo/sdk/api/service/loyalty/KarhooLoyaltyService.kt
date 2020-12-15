package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooLoyaltyService : LoyaltyService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun getBalance(loyaltyID: String): Call<LoyaltyBalance> = LoyaltyInteractor(credentialsManager, apiTemplate).apply {
        this.loyaltyId
    }
}
