package com.karhoo.sdk.api.datastore.credentials

import android.content.SharedPreferences

import com.karhoo.sdk.api.model.Credentials

class KarhooCredentialsManager(private val sharedPreferences: SharedPreferences) : CredentialsManager {

    override val credentials: Credentials
        get() = Credentials(sharedPreferences.getString(ACCESS_TOKEN, "").orEmpty(),
                            sharedPreferences.getString(REFRESH_TOKEN, "").orEmpty(),
                            sharedPreferences.getLong(EXPIRES_IN, 0L),
                            sharedPreferences.getLong(REFRESH_EXPIRES_IN, 0L),
                            sharedPreferences.getLong(RETRIEVAL_DATE, 0L))

    override val isValidToken: Boolean
        get() {
            val now = System.currentTimeMillis()
            val expiresIn = sharedPreferences.getLong(EXPIRES_IN, 0L)
            val retrievalDate = sharedPreferences.getLong(RETRIEVAL_DATE, 0L)
            return now < retrievalDate + (expiresIn * SECOND_MILLISECONDS)
        }

    override val isValidRefreshToken: Boolean
        get() {
            val now = System.currentTimeMillis()
            val refreshExpiresIn = sharedPreferences.getLong(REFRESH_EXPIRES_IN, 0L)
            val retrievalDate = sharedPreferences.getLong(RETRIEVAL_DATE, 0L)
            return now < retrievalDate + (refreshExpiresIn * SECOND_MILLISECONDS)
        }

    override fun saveCredentials(credentials: Credentials) {
        with(sharedPreferences.edit().putString(ACCESS_TOKEN, credentials.accessToken)) {
            if (!credentials.refreshToken.isEmpty()) {
                putString(REFRESH_TOKEN, credentials.refreshToken)
            }
            credentials.expiresIn?.let { putLong(EXPIRES_IN, it) }
            credentials.refreshExpiresIn?.let { putLong(REFRESH_EXPIRES_IN, it) }

            putLong(RETRIEVAL_DATE, credentials.retrievalDate)

            apply()
        }
    }

    override fun deleteCredentials() {
        sharedPreferences.edit()
                .putString(REFRESH_TOKEN, null)
                .putString(ACCESS_TOKEN, null)
                .putLong(EXPIRES_IN, 0L)
                .apply()
    }

    companion object {
        const val PREFERENCES_CRED_NAME = "credentials"
        const val SECOND_MILLISECONDS = 1000
        private const val REFRESH_TOKEN = "refresh_token"
        private const val ACCESS_TOKEN = "access_token"
        private const val EXPIRES_IN = "expires_in"
        private const val RETRIEVAL_DATE = "retrieval_date"
        private const val REFRESH_EXPIRES_IN = "refresh_expires_in"
    }

}
