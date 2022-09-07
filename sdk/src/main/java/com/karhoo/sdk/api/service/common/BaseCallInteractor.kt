package com.karhoo.sdk.api.service.common

import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.RefreshTokenRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.call.Call
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.util.Log
import com.karhoo.sdk.api.service.common.InteractorConstants.AUTH_TOKEN_REFRESH_NEEEDED
import kotlin.coroutines.CoroutineContext

internal abstract class BaseCallInteractor<RESPONSE> protected constructor(
    private val requestRequiresToken: Boolean,
    private val credentialsManager: CredentialsManager,
    private val apiTemplate: APITemplate,
    private val context: CoroutineContext
) : Call<RESPONSE> {

    internal abstract fun createRequest(): Deferred<Resource<RESPONSE>>

    override fun execute(subscriber: (Resource<RESPONSE>) -> Unit) {
        GlobalScope.launch(context) {
            val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod()
            if (shouldRefreshToken(config)) {
                if (!credentialsManager.isValidRefreshToken) {
                    /** Request an external login in order to refresh the credentials if the refresh token
                     * is not valid */
                    Log.i(TAG, AUTH_TOKEN_REFRESH_NEEEDED)

                    KarhooSDKConfigurationProvider.configuration.requestExternalAuthentication()
                } else {
                    when (val resource = refreshCredentials(config, apiTemplate, credentialsManager)) {
                        is Resource.Success -> successfulCredentials(resource.data, subscriber)
                        is Resource.Failure -> subscriber(Resource.Failure(resource.error))
                    }
                }
            } else {
                subscriber(createRequest().await())
            }
        }
    }

    private fun shouldRefreshToken(config: AuthenticationMethod): Boolean {
        return (config is AuthenticationMethod.KarhooUser || config is AuthenticationMethod.TokenExchange) &&
                requestRequiresToken && !credentialsManager.isValidToken
    }

    private suspend fun successfulCredentials(
        credentials: Credentials,
        subscriber: (Resource<RESPONSE>) -> Unit
    ) {
        val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod()
        credentialsManager.saveCredentials(credentials, apiTemplate, config)
        subscriber(createRequest().await())
    }

    companion object {
        private const val TAG = "BaseCallInteractor"
        private const val CLIENT_ID = "client_id"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val GRANT_TYPE = "grant_type"

        suspend fun refreshCredentials(config: AuthenticationMethod, apiTemplate: APITemplate, credentialsManager: CredentialsManager) : Resource<Credentials> {
            if (config is AuthenticationMethod.TokenExchange) {
                val authRefreshParams = mapOf(
                    Pair(CLIENT_ID, config.clientId),
                    Pair(REFRESH_TOKEN, credentialsManager.credentials.refreshToken),
                    Pair(GRANT_TYPE, REFRESH_TOKEN))

                return apiTemplate.authRefresh(authRefreshParams).await()
            }
            return apiTemplate.refreshToken(RefreshTokenRequest(credentialsManager.credentials.refreshToken)).await()
        }
    }
}
