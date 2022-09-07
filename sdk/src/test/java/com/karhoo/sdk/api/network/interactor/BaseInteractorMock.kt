package com.karhoo.sdk.api.network.interactor

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class BaseInteractorMock @Inject constructor(
    credentialsManager: CredentialsManager,
    apiTemplate: APITemplate,
    context: CoroutineContext = Dispatchers.Main
) : BaseCallInteractor<Any>(true, credentialsManager, apiTemplate, context) {

    lateinit var result: Resource<Any>
    override fun createRequest(): Deferred<Resource<Any>> {
        return CompletableDeferred(result)
    }


}
