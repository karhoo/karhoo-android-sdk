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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

class AuthLoginWithCredentialsInteractorTest {
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

    private lateinit var withCredentialsInteractor: AuthLoginWithCredentialsInteractor

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
        applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        withCredentialsInteractor = AuthLoginWithCredentialsInteractor(credentialsManager, userManager, apiTemplate, paymentsService, context)
        withCredentialsInteractor.credentials = credentials
    }

    @After
    fun tearDown() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
        applicationContext, authenticationMethod = AuthenticationMethod.KarhooUser()))
    }

    /**
     * When: When credentials are set and
     * Then: token endpoint is called
     */
    @Test
    fun `token endpoint is called is called when client id is set on auth config`() {
        runBlocking {
            withCredentialsInteractor.execute {}
            delay(100)
        }

        verify(apiTemplate).authUserInfo()
    }

    /**
     * When: NO Client id is set in auth config
     * Then: token endpoint is not called
     */
    @Test
    fun `token endpoint not called if no client id is set on auth config`() {

        KarhooSDKConfigurationProvider.setConfig(
                configuration = UnitTestSDKConfig(
                        context = applicationContext,
                        authenticationMethod = AuthenticationMethod.TokenExchange("", "scope")
                )
        )

        runBlocking {
            withCredentialsInteractor.execute {}
            delay(100)
        }

        verify(apiTemplate, never()).authToken(authTokenParams)
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
            withCredentialsInteractor.execute {}
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
            withCredentialsInteractor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(100)
        }

        verify(userManager, never()).saveUser(any())
        verify(paymentsService, never()).getPaymentProvider()
        Assert.assertEquals(KarhooError.GeneralRequestError, error)
        Assert.assertNull(shouldBeNull)
    }
}
