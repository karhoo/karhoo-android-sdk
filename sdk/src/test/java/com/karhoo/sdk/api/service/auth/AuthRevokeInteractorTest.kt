package com.karhoo.sdk.api.service.auth

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserStore
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.network.adapter.void
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.network.response.Resource.Failure
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class AuthRevokeInteractorTest {

    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val userStore: UserStore = mock()
    private val context: CoroutineContext = Dispatchers.Unconfined
    private val applicationContext: Context = mock()
    private lateinit var interactor: AuthRevokeInteractor

    private val clientId = "clientId"
    private val token = "123"

    private val authRevokeParams = mapOf(
            Pair("client_id", clientId),
            Pair("token_type_hint", "refresh_token"),
            Pair("token", token))

    @Before
    fun setUp() {
        whenever(credentialsManager.credentials).thenReturn(mock())
        whenever(credentialsManager.credentials.refreshToken).thenReturn(token)

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        interactor = AuthRevokeInteractor(credentialsManager, apiTemplate, userStore, context)
    }

    @After
    fun tearDown() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.KarhooUser()))
    }

    /**
     * When: revoking token succeeds
     * Then: correct result should be returned
     */
    @Test
    fun `revoke token Success`() {
        whenever(apiTemplate.authRevoke(authRevokeParams)).thenReturn(CompletableDeferred(Resource.Success(void())))
        var returnedRevokeResponse: Void? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedRevokeResponse = result.data
                    is Failure -> fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedRevokeResponse)
    }

    /**
     * When: revoking token succeeds
     * Then: error is propagated
     **/
    @Test
    fun `revoke token fails`() {
        var shouldBeNull: Void? = null
        var error: KarhooError? = null
        whenever(apiTemplate.authRevoke(authRevokeParams)).thenReturn(CompletableDeferred(Failure(KarhooError.GeneralRequestError)))

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Failure -> error = result.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.GeneralRequestError, error)
        assertNull(shouldBeNull)
    }
}
