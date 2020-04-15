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
import kotlin.coroutines.CoroutineContext

internal abstract class BaseCallInteractor<RESPONSE> protected constructor(private val requestRequiresToken: Boolean,
                                                                           private val credentialsManager: CredentialsManager,
                                                                           private val apiTemplate: APITemplate,
                                                                           private val context: CoroutineContext)
    : Call<RESPONSE> {

    internal abstract fun createRequest(): Deferred<Resource<RESPONSE>>

    override fun execute(subscriber: (Resource<RESPONSE>) -> Unit) {
        GlobalScope.launch(context) {
            val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod()
            if (shouldRefreshToken(config)) {
                when (val resource = refreshEndpoint(config).await()) {
                    is Resource.Success -> successfulCredentials(resource.data, subscriber)
                    is Resource.Failure -> subscriber(Resource.Failure(resource.error))
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

            return apiTemplate.authRefresh(authRefreshParams)
        }

        return apiTemplate.refreshToken(RefreshTokenRequest
                                        (credentialsManager.credentials
                                                 .refreshToken))
    }

    private fun shouldRefreshToken(config: AuthenticationMethod): Boolean {
        return (config is AuthenticationMethod.KarhooUser || config is AuthenticationMethod.TokenExchange) &&
                requestRequiresToken && !credentialsManager.isValidToken
    }

    private suspend fun successfulCredentials(credentials: Credentials, subscriber: (Resource<RESPONSE>) -> Unit) {
        credentialsManager.saveCredentials(credentials)
        subscriber(createRequest().await())
    }

}
