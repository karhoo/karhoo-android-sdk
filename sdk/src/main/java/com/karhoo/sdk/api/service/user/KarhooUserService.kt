package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.KarhooApi
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.datastore.user.UserStore
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.RefreshTokenRequest
import com.karhoo.sdk.api.network.request.UserDetailsUpdateRequest
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.request.UserRegistration
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooUserService : UserService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var userManager: UserManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    @Inject
    internal lateinit var analytics: Analytics

    @Inject
    internal lateinit var userStore: UserStore

    override fun register(userRegistration: UserRegistration): Call<UserInfo> {
        authSettingCheck()

        return UserRegisterInteractor(credentialsManager, apiTemplate).apply {
            this.userRegistration = userRegistration
        }
    }

    override fun loginUser(userLogin: UserLogin): Call<UserInfo> {
        authSettingCheck()

        return UserLoginInteractor(credentialsManager, userManager, apiTemplate, analytics,
                                   KarhooApi.paymentsService).apply {
            this.userLogin = userLogin
        }
    }

    override fun resetPassword(email: String): Call<Void> {
        authSettingCheck()

        return PasswordResetInteractor(credentialsManager, apiTemplate).apply {
            this.email = email
        }
    }

    override fun updateUserDetails(userDetailsUpdateRequest: UserDetailsUpdateRequest): Call<UserInfo> {
        authSettingCheck()

        return UserDetailsUpdateInteractor(credentialsManager, apiTemplate, analytics, userManager).apply {
            this.userDetailsUpdateRequest = userDetailsUpdateRequest
        }
    }

    override fun logout() {
        userStore.removeCurrentUser()
        clearRefreshToken()
    }

    override fun clearRefreshToken() {
        apiTemplate.clearRefreshToken()
    }

    private fun authSettingCheck() {
        if (KarhooSDKConfigurationProvider.configuration.authenticationMethod() is
                        AuthenticationMethod.TokenExchange) {
            throw RuntimeException("You have set an AuthenticationMethod in the KarhooSDKConfiguration " +
                                           "interface which forbids this UserService function")
        }
    }
}
