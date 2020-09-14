package com.karhoo.sdk.api

sealed class KarhooEnvironment {
    class Sandbox : KarhooEnvironment()
    class Production : KarhooEnvironment()
    class Custom(val host: String, val authHost: String, val guestHost:
    String) : KarhooEnvironment()
}
