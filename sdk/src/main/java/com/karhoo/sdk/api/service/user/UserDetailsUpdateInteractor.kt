package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.UserDetailsUpdateRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class UserDetailsUpdateInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                               private val apiTemplate: APITemplate,
                                                               private val analytics: Analytics,
                                                               private val userManager: UserManager,
                                                               context: CoroutineContext = Main)
    : BaseCallInteractor<UserInfo>(true, credentialsManager, apiTemplate, context) {

    var userDetailsUpdateRequest: UserDetailsUpdateRequest? = null

    override fun createRequest(): Deferred<Resource<UserInfo>> {
        userDetailsUpdateRequest?.let { request ->
            if (request.userId.isNotEmpty()) {
                return GlobalScope.async {
                    return@async updateUserDetails(request.userId, request)
                }
            } else {
                return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
            }
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

    private suspend fun updateUserDetails(userId: String, payload: UserDetailsUpdateRequest): Resource<UserInfo> {

        return when (val result = apiTemplate.userProfileUpdate(userId, payload).await()) {
            is Resource.Success -> {
                analytics.userInfo = result.data
                userManager.saveUser(result.data)
                Resource.Success(data = result.data)
            }
            is Resource.Failure -> {
                Resource.Failure(error = result.error)
            }
        }
    }

}