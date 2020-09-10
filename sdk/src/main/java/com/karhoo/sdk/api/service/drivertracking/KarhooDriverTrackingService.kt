package com.karhoo.sdk.api.service.drivertracking

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.call.PollCall
import javax.inject.Inject

class KarhooDriverTrackingService : DriverTrackingService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun trackDriver(tripIdentifier: String): PollCall<DriverTrackingInfo> =
            DriverTrackingInteractor(credentialsManager, apiTemplate).apply {
        this.tripIdentifier = tripIdentifier
    }

}
