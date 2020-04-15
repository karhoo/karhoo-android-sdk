package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class UserDetailsInteractor @Inject constructor(private val apiTemplate: APITemplate,
                                                         credentialsManager: CredentialsManager,
                                                         context: CoroutineContext = Main)
    : BaseCallInteractor<UserInfo>(true, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<UserInfo>> {
        return apiTemplate.userProfile()
    }
}
