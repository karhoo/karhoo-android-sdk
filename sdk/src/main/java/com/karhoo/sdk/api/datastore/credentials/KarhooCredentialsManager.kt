package com.karhoo.sdk.api.datastore.credentials

import android.content.SharedPreferences
import android.util.Log
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod

import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor.Companion.refreshCredentials
import com.karhoo.sdk.api.service.common.InteractorConstants
import com.karhoo.sdk.api.service.common.InteractorConstants.AUTH_TOKEN_REFRESH_NEEEDED
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class KarhooCredentialsManager(private val sharedPreferences: SharedPreferences) : CredentialsManager {
    private var credentialsRefreshTimer: Job? = null
    override val credentials: Credentials
        get() {
            val credentials = Credentials(
                sharedPreferences.getString(ACCESS_TOKEN, "").orEmpty(),
                sharedPreferences.getString(REFRESH_TOKEN, "").orEmpty(),
                sharedPreferences.getLong(EXPIRES_IN, 0L),
                sharedPreferences.getLong(REFRESH_EXPIRES_IN, 0L),
            )
            credentials.retrievalTimestamp = Date(sharedPreferences.getLong(RETRIEVAL_DATE, 0L))

            return credentials
        }

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
            return (now < retrievalDate + (refreshExpiresIn * SECOND_MILLISECONDS))
                    && sharedPreferences.getString(REFRESH_TOKEN, "").orEmpty().isNotEmpty()
        }

    override fun saveCredentials(credentials: Credentials, apiTemplate: APITemplate?, config: AuthenticationMethod?) {
        with(sharedPreferences.edit().putString(ACCESS_TOKEN, credentials.accessToken)) {
            if (credentials.refreshToken.isNullOrEmpty()) {
                putString(REFRESH_TOKEN, "")
            } else {
                putString(REFRESH_TOKEN, credentials.refreshToken)
            }
            putLong(EXPIRES_IN, credentials.expiresIn)
            credentials.refreshExpiresIn?.let { putLong(REFRESH_EXPIRES_IN, it) }

            putLong(RETRIEVAL_DATE, credentials.retrievalTimestamp?.time ?: 0)

            apply()

            launchCredentialsTimer(apiTemplate, config)
        }
    }

    private fun launchCredentialsTimer(apiTemplate: APITemplate?, config: AuthenticationMethod?) {
        /**
         * Schedule an automatic token refresh 
         */

        credentialsRefreshTimer?.cancel()
        credentialsRefreshTimer = null

        if (config != null && apiTemplate != null) {
            credentialsRefreshTimer = GlobalScope.launch {
                val modifier: Long = if(credentials.expiresIn <= MIN_REFRESH_BUFFER_SECONDS) {
                    REFRESH_BUFFER_MAX_PERCENTAGE_MODIFIER.toLong()
                } else {
                    REFRESH_BUFFER_MIN_PERCENTAGE_MODIFIER.toLong()
                }

                val refreshBufferMilis = credentials.expiresIn * modifier * SECOND_MILLISECONDS.toLong()
                delay((credentials.retrievalTimestamp?.time ?: 0) + (credentials.expiresIn * SECOND_MILLISECONDS) - refreshBufferMilis.toInt())

                if (!isValidRefreshToken) {
                    /** Request an external login in order to refresh the credentials if the refresh token
                     * is not valid */
                    Log.i(TAG, InteractorConstants.AUTH_TOKEN_TIMER_REFRESH)
                    requestExternalAuthentication()
                } else {
                    when (val resource =
                        refreshCredentials(config, apiTemplate, this@KarhooCredentialsManager)) {
                        is Resource.Success -> saveCredentials(resource.data, apiTemplate, config)
                        is Resource.Failure -> {
                            /** If refreshing the credentials with a refresh token has failed, then
                             * request and external login as a fail-safe
                             * */
                            Log.e(TAG, AUTH_TOKEN_REFRESH_NEEEDED)
                            requestExternalAuthentication()
                        }
                    }
                }
            }
        }
    }

    private suspend fun requestExternalAuthentication() {
        KarhooSDKConfigurationProvider.configuration.requireSDKAuthentication {}
    }

    override fun deleteCredentials() {
        sharedPreferences.edit()
                .putString(REFRESH_TOKEN, null)
                .putString(ACCESS_TOKEN, null)
                .putLong(EXPIRES_IN, 0L)
                .putLong(REFRESH_EXPIRES_IN, 0L)
                .putLong(RETRIEVAL_DATE, 0L)
                .apply()
    }

    companion object {
        const val PREFERENCES_CRED_NAME = "credentials"
        const val SECOND_MILLISECONDS = 1000
        const val MIN_REFRESH_BUFFER_SECONDS = 60
        const val REFRESH_BUFFER_MAX_PERCENTAGE_MODIFIER = 0.20f
        const val REFRESH_BUFFER_MIN_PERCENTAGE_MODIFIER = 0.5f
        private const val REFRESH_TOKEN = "refresh_token"
        private const val ACCESS_TOKEN = "access_token"
        private const val EXPIRES_IN = "expires_in"
        private const val RETRIEVAL_DATE = "retrieval_date"
        private const val REFRESH_EXPIRES_IN = "refresh_expires_in"
        private val TAG = KarhooCredentialsManager::class.java.name
    }

}
