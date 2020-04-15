package com.karhoo.sdk.api.util

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock

class ResponseUtils(val httpCode: Int,
                    val response: String,
                    val delayInMillis: Int = 0,
                    private val header: Pair<String, String> = Pair("", "")) {

    fun createResponse(): ResponseDefinitionBuilder {
        return WireMock.aResponse()
                .withStatus(httpCode)
                .withHeader("Content-Type", "json/application")
                .withHeader(header.first, header.second)
                .withBody(response)
                .withFixedDelay(delayInMillis)
    }

}