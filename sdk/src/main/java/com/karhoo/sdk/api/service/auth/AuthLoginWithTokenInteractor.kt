package com.karhoo.sdk.api.service.auth

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import com.karhoo.sdk.api.service.payments.PaymentsService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AuthLoginWithTokenInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                                private val userManager: UserManager,
                                                                private val apiTemplate: APITemplate,
                                                                private val paymentsService: PaymentsService,
                                                                private val context: CoroutineContext = Main)
    : BaseCallInteractor<UserInfo>(false, credentialsManager, apiTemplate, context) {

    internal var token: String? = null

    override fun createRequest(): Deferred<Resource<UserInfo>> {
        if (KarhooSDKConfigurationProvider.configuration.authenticationMethod() is
                        AuthenticationMethod.KarhooUser) {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }

        return GlobalScope.async {
            return@async getAuthToken()
        }
    }

    private suspend fun getAuthToken(): Resource<UserInfo> {
        val configuration = KarhooSDKConfigurationProvider.configuration.authenticationMethod()
                as AuthenticationMethod.TokenExchange
        val authTokenParams = mapOf(
                Pair(CLIENT_ID_KEY, configuration.clientId),
                Pair(SCOPE_KEY, configuration.scope),
                Pair(TOKEN_KEY, token.orEmpty()))
        return when (val authToken = apiTemplate.authToken(authTokenParams).await()) {
            is Resource.Success -> {
                credentialsManager.saveCredentials(authToken.data, apiTemplate, configuration)
                getUser()
            }
            is Resource.Failure -> Resource.Failure(authToken.error)
        }
    }

    private suspend fun getUser(): Resource<UserInfo> {
        return when (val user = apiTemplate.authUserInfo().await()) {
            is Resource.Success -> {
                userManager.saveUser(user.data)
                paymentsService.getPaymentProvider().execute { }
                Resource.Success(user.data)
            }
            is Resource.Failure -> Resource.Failure(user.error)
        }
    }

    companion object {
        private const val CLIENT_ID_KEY = "client_id"
        private const val SCOPE_KEY = "scope"
        private const val TOKEN_KEY = "token"
    }
}
