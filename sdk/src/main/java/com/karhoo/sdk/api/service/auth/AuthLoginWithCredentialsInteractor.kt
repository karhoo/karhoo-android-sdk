package com.karhoo.sdk.api.service.auth

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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class AuthLoginWithCredentialsInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                                      private val userManager: UserManager,
                                                                      private val apiTemplate: APITemplate,
                                                                      private val paymentsService: PaymentsService,
                                                                      private val context: CoroutineContext = Dispatchers.Main)
    : BaseCallInteractor<UserInfo>(false, credentialsManager, apiTemplate, context) {
    internal var credentials: Credentials? = null
        set(value) {
            value?.let {
                credentialsManager.saveCredentials(it, apiTemplate, KarhooSDKConfigurationProvider.configuration.authenticationMethod())
            }
            field = value
        }

    override fun createRequest(): Deferred<Resource<UserInfo>> {
        return GlobalScope.async {
            return@async getUser()
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
}
