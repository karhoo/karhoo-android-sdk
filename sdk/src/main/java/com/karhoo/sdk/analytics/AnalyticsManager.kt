package com.karhoo.sdk.analytics

import android.util.Log
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.model.UserInfo

object AnalyticsManager : Analytics, AnalyticsPayload {

    private var analytics: MutableSet<AnalyticProvider> = mutableSetOf()

    override var deviceId: String = ""

    override var sessionId: String = ""
    override var usersLatLng: Position? = null
    override var userInfo: UserInfo? = null
    private var isGuestMode: Boolean = false

    override fun initialise() {
        KarhooSDKConfigurationProvider.configuration
                .analyticsProvider()?.let {
                    analytics.add(it)
                } ?: Log.e(this::class.java.simpleName, "No analytics provider set")
    }

    override fun fireEvent(event: Event) {
        fireEvent(event, Payloader.Builder.builder.build())
    }

    override fun setGuestMode(isGuestMode: Boolean) {
        this.isGuestMode = isGuestMode
    }

    override fun fireEvent(event: Event, payload: Payloader) {
        payload.apply {
            setSessionTokens(deviceId, sessionId)
            setGuestMode(isGuestMode)
            userInfo?.let { setUser(it) }
            usersLatLng?.let { setUserLatLng(it) }
        }
        informProviders(event.value, payload)
    }

    private fun informProviders(event: String, payloader: Payloader) {
        analytics.forEach { it.trackEvent(event, payloader.payload) }
    }

}
