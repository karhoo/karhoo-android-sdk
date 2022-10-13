package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import com.karhoo.sdk.api.service.common.InteractorConstants
import com.karhoo.sdk.api.service.payments.PaymentsService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class UserLoginInteractor @Inject constructor(private val credentialsManager: CredentialsManager,
                                                       private val userManager: UserManager,
                                                       private val apiTemplate: APITemplate,
                                                       private val analytics: Analytics,
                                                       private val paymentsService: PaymentsService,
                                                       context: CoroutineContext = Main)
    : BaseCallInteractor<UserInfo>(false, credentialsManager, apiTemplate, context) {

    var userLogin: UserLogin? = null

    override fun createRequest(): Deferred<Resource<UserInfo>> {
        userLogin?.let {
            return GlobalScope.async {
                return@async loginUser(it)
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun loginUser(userLogin: UserLogin): Resource<UserInfo> {
        if (userManager.isUserStillValid) {
            return Resource.Failure(error = KarhooError.UserAlreadyLoggedIn)
        }

        return when (val credentials = apiTemplate.login(userLogin).await()) {
            is Resource.Success -> userProfile(credentials.data)
            is Resource.Failure -> Resource.Failure(error = credentials.error)
        }
    }

    private suspend fun userProfile(credentials: Credentials): Resource<UserInfo> {
        credentialsManager.saveCredentials(credentials, apiTemplate, KarhooSDKConfigurationProvider.configuration.authenticationMethod())
        return when (val userInfo = apiTemplate.userProfile().await()) {
            is Resource.Success -> checkIfRolesAreValidAndSave(userInfo.data)
            is Resource.Failure -> userInfo
        }
    }

    private fun checkIfRolesAreValidAndSave(userInfo: UserInfo): Resource<UserInfo> {
        userInfo.organisations.forEach {
            it.roles?.let { roles ->
                if (roles.contains(InteractorConstants.MOBILE_USER)
                        || roles.contains(InteractorConstants.REQUIRED_ROLE)) {
                    onSuccessfulUser(userInfo)
                    return Resource.Success(userInfo)
                }
            }
        }
        credentialsManager.deleteCredentials()
        return Resource.Failure(error = KarhooError.RequiredRolesNotAvailable)
    }

    private fun onSuccessfulUser(userInfo: UserInfo) {
        analytics.userInfo = userInfo
        userManager.saveUser(userInfo)
        paymentsService.getPaymentProvider().execute {}
    }
}
