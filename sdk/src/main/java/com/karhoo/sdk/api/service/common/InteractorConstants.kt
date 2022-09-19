package com.karhoo.sdk.api.service.common

internal object InteractorConstants {
    const val REQUIRED_ROLE = "TRIP_ADMIN"
    const val MOBILE_USER = "MOBILE_USER"
    const val AUTH_TOKEN_REFRESH_NEEEDED = "An authentication token refresh is needed, please login again and invoke requestExternalAuthentication's callback when the process is complete"
    const val AUTH_TOKEN_TIMER_REFRESH = "The access token has expired, attempting to refresh it in background"
}
