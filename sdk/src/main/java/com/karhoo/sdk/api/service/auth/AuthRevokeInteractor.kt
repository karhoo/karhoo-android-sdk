package com.karhoo.sdk.api.service.auth

import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserStore
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BasePollCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AuthRevokeInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                        private val apiTemplate: APITemplate,
                                                        private val userStore: UserStore,
                                                        private val context: CoroutineContext = Main)
    : BasePollCallInteractor<Void>(false, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<Void>> {
        return GlobalScope.async {
            return@async revokeAndRemoveUser()
        }
    }

    private suspend fun revokeAndRemoveUser(): Resource<Void> {
        val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod() as
                AuthenticationMethod.TokenExchange
        val authRevokeRequest = mapOf(
                Pair("client_id", config.clientId),
                Pair("token_type_hint", "refresh_token"),
                Pair("token", credentialsManager.credentials.refreshToken))

        val revokeResponse = apiTemplate.authRevoke(authRevokeRequest).await()
        userStore.removeCurrentUser()
        return revokeResponse
    }
}
