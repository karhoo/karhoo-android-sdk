package com.karhoo.sdk.api

import com.karhoo.sdk.BuildConfig

internal data class EnvironmentDetails(val environment: KarhooEnvironment) {
    var host: String = ""
    var authHost: String = ""
    var guestHost: String = ""

    init {
        when (environment) {
            is KarhooEnvironment.Sandbox -> {
                this.host = BuildConfig.KARHOO_API_POINT_SANDBOX
                this.authHost = BuildConfig.KARHOO_AUTH_API_POINT_SANDBOX
                this.guestHost = BuildConfig.KARHOO_GUEST_API_POINT_SANDBOX
            }
            is KarhooEnvironment.Production -> {
                this.host = BuildConfig.KARHOO_API_POINT_PROD
                this.authHost = BuildConfig.KARHOO_AUTH_API_POINT_PROD
                this.guestHost = BuildConfig.KARHOO_GUEST_API_POINT_PROD
            }
            is KarhooEnvironment.Custom -> {
                this.host = environment.host
                this.authHost = environment.authHost
                this.guestHost = environment.guestHost
            }
        }
    }

    companion object {
        fun current(): EnvironmentDetails {
            return EnvironmentDetails(KarhooSDKConfigurationProvider.configuration.environment())
        }
    }

}
