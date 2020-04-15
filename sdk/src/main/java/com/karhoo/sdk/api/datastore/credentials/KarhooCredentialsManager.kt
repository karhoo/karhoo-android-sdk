package com.karhoo.sdk.api.datastore.credentials

import android.content.SharedPreferences

import com.karhoo.sdk.api.model.Credentials

class KarhooCredentialsManager(private val sharedPreferences: SharedPreferences) : CredentialsManager {

    override val credentials: Credentials
        get() = Credentials(sharedPreferences.getString(ACCESS_TOKEN, "").orEmpty(),
                            sharedPreferences.getString(REFRESH_TOKEN, "").orEmpty(),
                            sharedPreferences.getLong(EXPIRES_ON, 0L),
                            sharedPreferences.getLong(REFRESH_EXPIRES_ON, 0L))

    override val isValidToken: Boolean
        get() {
            val now = System.currentTimeMillis()
            val expiresOn = sharedPreferences.getLong(EXPIRES_ON, 0L)
            return now < expiresOn
        }

    override val isValidRefreshToken: Boolean
        get() {
            val now = System.currentTimeMillis()
            val refreshExpiresOn = sharedPreferences.getLong(REFRESH_EXPIRES_ON, 0L)
            return now < refreshExpiresOn
        }

    override fun saveCredentials(credentials: Credentials) {
        with(sharedPreferences.edit().putString(ACCESS_TOKEN, credentials.accessToken)) {
            if (!credentials.refreshToken.isEmpty()) {
                putString(REFRESH_TOKEN, credentials.refreshToken)
            }
            credentials.expiresIn?.let { putLong(EXPIRES_ON, System.currentTimeMillis() + (it * 1000)) }
            credentials.refreshExpiresIn?.let { putLong(REFRESH_EXPIRES_ON, System.currentTimeMillis() + (it * 1000)) }
            apply()
        }
    }

    override fun deleteCredentials() {
        sharedPreferences.edit()
                .putString(REFRESH_TOKEN, null)
                .putString(ACCESS_TOKEN, null)
                .putLong(EXPIRES_ON, 0L)
                .apply()
    }

    companion object {
        const val PREFERENCES_CRED_NAME = "credentials"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val ACCESS_TOKEN = "access_token"
        private const val EXPIRES_ON = "expires_on"
        private const val REFRESH_EXPIRES_ON = "refresh_expires_on"
    }

}
