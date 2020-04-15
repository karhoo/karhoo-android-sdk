package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.ResetPasswordRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class PasswordResetInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                           private val apiTemplate: APITemplate,
                                                           context: CoroutineContext = Main)
    : BaseCallInteractor<Void>(false, credentialsManager, apiTemplate, context) {

    var email: String? = null

    override fun createRequest(): Deferred<Resource<Void>> {
        email?.let { email ->
            return apiTemplate.passwordReset(ResetPasswordRequest(email = email))
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }
}