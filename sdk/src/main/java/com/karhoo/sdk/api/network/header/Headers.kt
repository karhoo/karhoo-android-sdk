package com.karhoo.sdk.api.network.header

interface Headers {

    fun generateCorrelationId(method: String): String

    val authenticationToken: String

    val contentType: String

}