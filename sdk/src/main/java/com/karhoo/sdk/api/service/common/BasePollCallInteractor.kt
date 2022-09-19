package com.karhoo.sdk.api.service.common

import android.util.Log
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.observable.KarhooObservable
import com.karhoo.sdk.api.network.observable.Observable
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.InteractorConstants.AUTH_TOKEN_REFRESH_NEEEDED
import com.karhoo.sdk.call.PollCall
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BasePollCallInteractor<RESPONSE> protected constructor(private val requestRequiresToken: Boolean,
                                                                      private val credentialsManager: CredentialsManager,
                                                                      private val apiTemplate: APITemplate,
                                                                      private val context: CoroutineContext)
    : PollCall<RESPONSE> {

    internal abstract fun createRequest(): Deferred<Resource<RESPONSE>>

    override fun execute(subscriber: (Resource<RESPONSE>) -> Unit) {
        GlobalScope.launch(context) {
            val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod()
            if (shouldRefreshToken(config)) {
                if (!credentialsManager.isValidRefreshToken) {
                    /** Request an external login in order to refresh the credentials if the refresh token
                     * is not valid */
                    Log.i(TAG, AUTH_TOKEN_REFRESH_NEEEDED)

                    requestExternalAuthentication(subscriber)
                } else {
                    when (val resource = BaseCallInteractor.refreshCredentials(config, apiTemplate, credentialsManager)) {
                        is Resource.Success -> successfulCredentials(resource.data, subscriber)
                        is Resource.Failure -> requestExternalAuthentication(subscriber)
                    }
                }
            } else {
                subscriber(createRequest().await())
            }
        }
    }

    override fun observable(): Observable<RESPONSE> {
        return KarhooObservable(this)
    }

    private fun shouldRefreshToken(config: AuthenticationMethod): Boolean {
        return (config is AuthenticationMethod.KarhooUser || config is AuthenticationMethod.TokenExchange) &&
                requestRequiresToken && !credentialsManager.isValidToken
    }

    private suspend fun successfulCredentials(credentials: Credentials, subscriber: (Resource<RESPONSE>) -> Unit) {
        val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod()
        credentialsManager.saveCredentials(credentials, apiTemplate, config)
        subscriber(createRequest().await())
    }

    private suspend fun requestExternalAuthentication(subscriber: (Resource<RESPONSE>) -> Unit) {
        var refreshTimedOut = false
        val replyTimer = GlobalScope.launch(context) {
            delay(BaseCallInteractor.ERROR_DELAY_SECONDS)
            subscriber(Resource.Failure(KarhooError.AuthenticationRequired))
            refreshTimedOut = true
        }

        val delayedRequest = RequestsQueue.DelayedRequest(subscriber, this)
        RequestsQueue.addRequest(delayedRequest as RequestsQueue.DelayedRequest<Any>)

        KarhooSDKConfigurationProvider.configuration.requestExternalAuthentication {
            replyTimer.cancel()
            if(!refreshTimedOut) {
                RequestsQueue.consumeRequests()
            }
        }
    }

    companion object {
        private const val TAG = "BasePollCallInteractor"
    }
}
