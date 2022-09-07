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

class KarhooCredentialsManager(private val sharedPreferences: SharedPreferences) : CredentialsManager {
    private var credentialsRefreshTimer: Job? = null
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
            return (now < retrievalDate + (refreshExpiresIn * SECOND_MILLISECONDS))
                    && sharedPreferences.getString(REFRESH_TOKEN, "").orEmpty().isNotEmpty()
        }

    override fun saveCredentials(credentials: Credentials, apiTemplate: APITemplate?, config: AuthenticationMethod?) {
        with(sharedPreferences.edit().putString(ACCESS_TOKEN, credentials.accessToken)) {
            if (credentials.refreshToken.isNotEmpty()) {
                putString(REFRESH_TOKEN, credentials.refreshToken)
            }
            putLong(EXPIRES_IN, credentials.expiresIn)
            credentials.refreshExpiresIn?.let { putLong(REFRESH_EXPIRES_IN, it) }

            putLong(RETRIEVAL_DATE, credentials.retrievalTimestamp)

            apply()

            /**
             * Schedule an automatic token refresh
             */
            credentialsRefreshTimer?.cancel()
            credentialsRefreshTimer = null

            if(config != null && apiTemplate != null) {
                credentialsRefreshTimer = GlobalScope.launch {
                    delay(credentials.retrievalTimestamp + (credentials.expiresIn * SECOND_MILLISECONDS) - REFRESH_BUFFER_MILLISECONDS)

                    if (!isValidRefreshToken) {
                        /** Request an external login in order to refresh the credentials if the refresh token
                         * is not valid */
                        Log.i(TAG, InteractorConstants.AUTH_TOKEN_TIMER_REFRESH)

                        KarhooSDKConfigurationProvider.configuration.requestExternalAuthentication()
                    } else {
                        when (val resource = refreshCredentials(config,apiTemplate, this@KarhooCredentialsManager)) {
                            is Resource.Success -> saveCredentials(resource.data, apiTemplate, config)
                            is Resource.Failure -> {
                                /** If refreshing the credentials with a refresh token has failed, then
                                 * request and external login as a fail-safe
                                 * */
                                Log.e(TAG, AUTH_TOKEN_REFRESH_NEEEDED)
                                KarhooSDKConfigurationProvider.configuration.requestExternalAuthentication()
                            }

                        }
                    }
                }

            }
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
        const val REFRESH_BUFFER_MILLISECONDS = 5 * 60 * 1000
        private const val REFRESH_TOKEN = "refresh_token"
        private const val ACCESS_TOKEN = "access_token"
        private const val EXPIRES_IN = "expires_in"
        private const val RETRIEVAL_DATE = "retrieval_date"
        private const val REFRESH_EXPIRES_IN = "refresh_expires_in"
        private val TAG = KarhooCredentialsManager::class.java.name
    }

}
