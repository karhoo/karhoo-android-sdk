package com.karhoo.sdk.api.service.auth

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.payments.PaymentsService
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class AuthLoginWithTokenInteractorTest {

    private val credentialsManager: CredentialsManager = mock()
    private val userManager: UserManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val paymentsService: PaymentsService = mock()
    private val context: CoroutineContext = Dispatchers.Unconfined
    private val applicationContext: Context = mock()
    private val userInfo: UserInfo = mock()

    private val credentials = Credentials(accessToken = "123456", refreshToken = "zxy", expiresIn = 1L)
    private val clientId = "clientId"
    private val scope = "scope"
    private val authTokenParams = mapOf(
            Pair("client_id", clientId),
            Pair("scope", scope),
            Pair("token", ""))

    @Captor
    private lateinit var credentialsCaptor: ArgumentCaptor<Credentials>

    private lateinit var withTokenInteractor: AuthLoginWithTokenInteractor

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        withTokenInteractor = AuthLoginWithTokenInteractor(credentialsManager, userManager, apiTemplate, paymentsService, context)
    }

    @After
    fun tearDown() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.KarhooUser()))
    }

    /**
     * When: auth config is in legacy mode
     * Then: then an error is thrown
     */
    @Test
    fun `an error is thrown when in auth config legacy mode`() {
        var error: KarhooError? = null
        var shouldBeNull: UserInfo? = null

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.KarhooUser()))
        withTokenInteractor = AuthLoginWithTokenInteractor(credentialsManager, userManager, apiTemplate, paymentsService, context)

        runBlocking {
            withTokenInteractor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(100)
        }

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
    }

    /**
     * When: Client id is set in auth config
     * Then: token endpoint is called
     */
    @Test
    fun `token endpoint is called is called when client id is set on auth config`() {
        runBlocking {
            withTokenInteractor.execute {}
            delay(100)
        }

        verify(apiTemplate).authToken(authTokenParams)
    }

    /**
     * When: NO Client id is set in auth config
     * Then: token endpoint is not called
     */
    @Test
    fun `token endpoint not called if no client id is set on auth config`() {

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("", "scope")))
        withTokenInteractor = AuthLoginWithTokenInteractor(credentialsManager, userManager, apiTemplate, paymentsService, context)

        runBlocking {
            withTokenInteractor.execute {}
            delay(100)
        }

        verify(apiTemplate, never()).authToken(authTokenParams)
    }

    /**
     * Given: auth token request
     * When: request succeeds
     * Then: user request is sent
     * And: Credentials are saved
     */
    @Test
    fun `credentials are saved on the credentials manager on successful token request`() {
        whenever(apiTemplate.authToken(authTokenParams))
                .thenReturn(CompletableDeferred(Resource.Success(credentials)))
        whenever(apiTemplate.authUserInfo())
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))
        doNothing().whenever(credentialsManager).saveCredentials(any())

        runBlocking {
            withTokenInteractor.execute {}
            delay(100)
        }

        verify(credentialsManager).saveCredentials(capture(credentialsCaptor))

        assertEquals("123456", credentialsCaptor.value.accessToken)
        assertEquals("zxy", credentialsCaptor.value.refreshToken)
        assertEquals(1f, credentialsCaptor.value.expiresIn?.toFloat() ?: -1f, 0f)
    }

    /**
     * Given: auth token request
     * When: request fails
     * Then: credentials should not be saved
     * And: user request should not be sent, error should be propagated
     */
    @Test
    fun `credentials are not saved and the error is propagated on token request failure`() {
        var shouldBeNull: UserInfo? = null
        var error: KarhooError? = null

        whenever(apiTemplate.authToken(authTokenParams))
                .thenReturn(CompletableDeferred(Resource.Failure(KarhooError.GeneralRequestError)))

        runBlocking {
            withTokenInteractor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(100)
        }

        verify(credentialsManager, never()).saveCredentials(any())
        verify(userManager, never()).saveUser(any())
        assertEquals(KarhooError.GeneralRequestError, error)
        assertNull(shouldBeNull)
    }

    /**
     * When: user request succeeds
     * Then: user is saved
     */
    @Test
    fun `user info is saved when user request succeeds`() {
        whenever(apiTemplate.authToken(authTokenParams))
                .thenReturn(CompletableDeferred(Resource.Success(credentials)))
        whenever(apiTemplate.authUserInfo())
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))
        doNothing().whenever(credentialsManager).saveCredentials(any())

        runBlocking {
            withTokenInteractor.execute {}
            delay(100)
        }

        verify(userManager, atLeastOnce()).saveUser(userInfo)
        verify(paymentsService, atLeastOnce()).getPaymentProvider()
    }

    /**
     * When: user request fails
     * Then: error propagated
     */
    @Test
    fun `error is propagated when user request fails`() {
        var shouldBeNull: UserInfo? = null
        var error: KarhooError? = null

        whenever(apiTemplate.authToken(authTokenParams))
                .thenReturn(CompletableDeferred(Resource.Success(credentials)))
        whenever(apiTemplate.authUserInfo())
                .thenReturn(CompletableDeferred(Resource.Failure(KarhooError.GeneralRequestError)))
        doNothing().whenever(credentialsManager).saveCredentials(any())

        runBlocking {
            withTokenInteractor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(100)
        }

        verify(userManager, never()).saveUser(any())
        verify(paymentsService, never()).getPaymentProvider()
        assertEquals(KarhooError.GeneralRequestError, error)
        assertNull(shouldBeNull)
    }
}
