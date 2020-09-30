package com.karhoo.sdk.api.network.header

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import java.util.UUID

class KarhooHeaders(private val credentialsManager: CredentialsManager) : Headers {

    override val authenticationToken: String
        get() = credentialsManager.credentials.accessToken

    override val contentType: String
        get() = CONTENT_TYPE

    private var lastQuoteId: String = ""

    override fun generateCorrelationId(method: String): String {
        return when (method) {
            APITemplate.BOOKING_WITH_NONCE_METHOD -> configuredQuoteId()
            APITemplate.QUOTES_REQUEST_METHOD -> updatedQuoteId()
            else -> ANDROID + UUID.randomUUID()
        }
    }

    private fun updatedQuoteId(): String {
        lastQuoteId = ANDROID + UUID.randomUUID()
        return lastQuoteId
    }

    private fun configuredQuoteId(): String {
        if (lastQuoteId.isEmpty()) {
            lastQuoteId = ANDROID + UUID.randomUUID()
        }
        return lastQuoteId
    }

    companion object {
        private const val ANDROID = "ANDROID-"
        private const val CONTENT_TYPE = "application/json"
    }

}
