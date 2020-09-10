package com.karhoo.sdk.api.service.config

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.UIConfig
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import com.karhoo.sdk.api.service.config.ui.UIConfigProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class UIConfigInteractor @Inject constructor(credentialsManager: CredentialsManager,
                                                      apiTemplate: APITemplate,
                                                      private val uiConfigProvider: UIConfigProvider,
                                                      private val userManager: UserManager,
                                                      val context: CoroutineContext = Main)
    : BaseCallInteractor<UIConfig>(true, credentialsManager, apiTemplate, context) {

    var uiConfigRequest: UIConfigRequest? = null

    override fun createRequest(): Deferred<Resource<UIConfig>> {
        uiConfigRequest?.let {
            if (userManager.user.organisations[0].id.isBlank()) {
                return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
            }
            return uiConfigProvider.fetchConfig(it, userManager.user.organisations[0])
        } ?: run {
            return CompletableDeferred(Resource.Failure(error = KarhooError.InternalSDKError))
        }
    }

}
