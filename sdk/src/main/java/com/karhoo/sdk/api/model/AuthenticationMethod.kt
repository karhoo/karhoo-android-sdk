package com.karhoo.sdk.api.model

sealed class AuthenticationMethod {
    class KarhooUser : AuthenticationMethod()
    class TokenExchange(val clientId: String, val scope: String) :
            AuthenticationMethod()
    class Guest(val identifier: String, val referer: String, val organisationId: String) :
            AuthenticationMethod()
}
