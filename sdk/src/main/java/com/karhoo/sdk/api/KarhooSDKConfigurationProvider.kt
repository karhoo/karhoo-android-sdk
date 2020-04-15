package com.karhoo.sdk.api

internal object KarhooSDKConfigurationProvider {

    lateinit var configuration: KarhooSDKConfiguration

    fun setConfig(configuration: KarhooSDKConfiguration) {
        this.configuration = configuration
    }

}
