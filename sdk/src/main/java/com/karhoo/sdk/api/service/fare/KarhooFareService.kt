package com.karhoo.sdk.api.service.fare

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooFareService : FareService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun fareDetails(tripId: String): Call<Fare> = FareInteractor(credentialsManager, apiTemplate).apply {
        this.tripId = tripId
    }

}
