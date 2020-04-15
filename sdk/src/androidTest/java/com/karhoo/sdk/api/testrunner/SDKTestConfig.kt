package com.karhoo.sdk.api.testrunner

import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

object SDKTestConfig {

    const val PORT_NUMBER: Int = 8089
    const val REST_API_LINK = "http://127.0.0.1:$PORT_NUMBER"

    val wireMockOptions = WireMockConfiguration.options()
            .port(SDKTestConfig.PORT_NUMBER)
            .notifier(ConsoleNotifier(true))

    const val DEVICE_LAT = 37.4219983
    const val DEVICE_LNG = -122.084

    const val APPLICATION_ID = "com.karhoo.uisdk.test"

}

