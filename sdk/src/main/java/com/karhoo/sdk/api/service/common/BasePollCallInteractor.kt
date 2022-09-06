package com.karhoo.sdk.api.service.common

import android.util.Log
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.observable.KarhooObservable
import com.karhoo.sdk.api.network.observable.Observable
import com.karhoo.sdk.api.network.request.RefreshTokenRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.InteractorContants.AUTH_TOKEN_REFRESH_NEEEDED
import com.karhoo.sdk.call.PollCall
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BasePollCallInteractor<RESPONSE> protected constructor(private val requestRequiresToken: Boolean,
                                                                      private val credentialsManager: CredentialsManager,
                                                                      private val deferredAPITemplate: APITemplate,
                                                                      private val context: CoroutineContext)
    : PollCall<RESPONSE> {

    internal abstract fun createRequest(): Deferred<Resource<RESPONSE>>

    override fun execute(subscriber: (Resource<RESPONSE>) -> Unit) {
        GlobalScope.launch(context) {
            val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod()
            if (shouldRefreshToken(config)) {
                if (credentialsManager.credentials.refreshToken.isEmpty()) {
                    /** Request a new access token from an external source if the refresh token
                     * is not correct and attempt to overwrite the old access token
                     */
                    Log.e(TAG, AUTH_TOKEN_REFRESH_NEEEDED)

                    KarhooSDKConfigurationProvider.configuration.requestNewAuthenticationCredentials { newCredentials ->
                        successfulCredentials(newCredentials, subscriber)
                    }
                } else {
                    when (val resource = refreshEndpoint(config).await()) {
                        is Resource.Success -> successfulCredentials(resource.data, subscriber)
                        is Resource.Failure -> subscriber(Resource.Failure(resource.error))
                    }
                }
            } else {
                subscriber(createRequest().await())
            }
        }
    }

    private fun refreshEndpoint(config: AuthenticationMethod): Deferred<Resource<Credentials>> {
        if (config is AuthenticationMethod.TokenExchange) {
            val authRefreshParams = mapOf(
                    Pair("client_id", config.clientId),
                    Pair("refresh_token", credentialsManager.credentials.refreshToken),
                    Pair("grant_type", "refresh_token"))

            return deferredAPITemplate.authRefresh(authRefreshParams)
        }
        return deferredAPITemplate.refreshToken(RefreshTokenRequest(credentialsManager.credentials.refreshToken))
    }

    override fun observable(): Observable<RESPONSE> {
        return KarhooObservable(this)
    }

    private fun shouldRefreshToken(config: AuthenticationMethod): Boolean {
        return (config is AuthenticationMethod.KarhooUser || config is AuthenticationMethod.TokenExchange) &&
                requestRequiresToken && !credentialsManager.isValidToken
    }

    private suspend fun successfulCredentials(credentials: Credentials, subscriber: (Resource<RESPONSE>) -> Unit) {
        credentialsManager.saveCredentials(credentials)
        subscriber(createRequest().await())
    }

    companion object {
        private const val TAG = "BasePollCallInteractor"
    }
}
