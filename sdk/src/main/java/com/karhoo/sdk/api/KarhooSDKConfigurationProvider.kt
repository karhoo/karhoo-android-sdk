package com.karhoo.sdk.api

import com.karhoo.sdk.analytics.AnalyticsManager
import com.karhoo.sdk.api.model.AuthenticationMethod

internal object KarhooSDKConfigurationProvider {

    lateinit var configuration: KarhooSDKConfiguration

    fun setConfig(configuration: KarhooSDKConfiguration) {
        this.configuration = configuration
        AnalyticsManager.setGuestMode(configuration.authenticationMethod() is AuthenticationMethod.Guest)
    }

}
