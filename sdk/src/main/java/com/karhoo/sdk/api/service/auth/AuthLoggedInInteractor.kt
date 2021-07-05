package com.karhoo.sdk.api.service.auth

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
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

internal class AuthLoggedInInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                          private val userManager: UserManager,
                                                          private val apiTemplate: APITemplate,
                                                          private val paymentsService: PaymentsService,
                                                          context: CoroutineContext = Main)
    : BaseCallInteractor<UserInfo>(false, credentialsManager, apiTemplate, context) {

    internal var credentials: Credentials? = null

    override fun createRequest(): Deferred<Resource<UserInfo>> {
        if (KarhooSDKConfigurationProvider.configuration.authenticationMethod() is
                        AuthenticationMethod.KarhooUser) {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }

        if (credentials == null) {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }

        return GlobalScope.async {
            return@async getAuthToken()
        }
    }

    private suspend fun getAuthToken(): Resource<UserInfo> {
        credentials?.let {
            credentialsManager.saveCredentials(it)
            return getUser()
        }
        return Resource.Failure(error = KarhooError.InternalSDKError)
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
}
