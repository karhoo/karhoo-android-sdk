package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.UserRegistration
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class UserRegisterInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                          private val apiTemplate: APITemplate,
                                                          context: CoroutineContext = Main)
    : BaseCallInteractor<UserInfo>(false, credentialsManager, apiTemplate, context) {

    var userRegistration: UserRegistration? = null

    override fun createRequest(): Deferred<Resource<UserInfo>> {
        userRegistration?.let { userRegistration ->
            val updatedRegistration = userRegistration.copy(locale = Locale.getDefault().toLanguageTag())
            return apiTemplate.register(updatedRegistration)
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
