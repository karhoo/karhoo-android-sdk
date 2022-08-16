package com.karhoo.sdk.api

import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

object SDKTestConfig {

    const val PORT_NUMBER: Int = 8089
    const val REST_API_LINK = "http://127.0.0.1"

    val wireMockOptions: WireMockConfiguration = WireMockConfiguration.options()
        .port(PORT_NUMBER)
        .notifier(ConsoleNotifier(true))
}

