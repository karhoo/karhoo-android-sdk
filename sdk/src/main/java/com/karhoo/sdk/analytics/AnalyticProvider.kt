package com.karhoo.sdk.analytics

interface AnalyticProvider {

    fun trackEvent(event: String)

    fun trackEvent(event: String, payloadMap: Map<String, Any>)

}
