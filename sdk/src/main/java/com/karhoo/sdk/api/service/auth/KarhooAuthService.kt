package com.karhoo.sdk.api.service.auth

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.datastore.user.UserStore
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooAuthService : AuthService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    @Inject
    internal lateinit var userStore: UserStore

    @Inject
    internal lateinit var userManager: UserManager

    override fun login(token: String): Call<UserInfo> = AuthLoginInteractor(credentialsManager = credentialsManager,
            apiTemplate = apiTemplate,
            userManager = userManager).apply {
        this.token = token
    }

    override fun revoke(): Call<Void> {
        return AuthRevokeInteractor(credentialsManager = credentialsManager, apiTemplate = apiTemplate, userStore = userStore)
    }
}