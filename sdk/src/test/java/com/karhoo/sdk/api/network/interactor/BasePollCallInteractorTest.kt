package com.karhoo.sdk.api.network.interactor

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

class BasePollCallInteractorTest {
    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val context: CoroutineContext = Dispatchers.Unconfined
    private val applicationContext: Context = mock()

    private val credentials = Credentials(
        accessToken = "123456",
        refreshToken = "zxy",
        expiresIn = 1L,
        refreshExpiresIn = 1L
    )
    private val newCredentials = Credentials(
        accessToken = "new123456",
        refreshToken = "new",
        expiresIn = 10L,
        refreshExpiresIn = 10L
    )

    private val credentialsWithoutRefresh = Credentials(
        accessToken = "123456",
        refreshToken = "",
        expiresIn = 1L,
        refreshExpiresIn = 1L
    )

    private val credentialsWithoutRefreshExpirationTime = Credentials(
        accessToken = "123456",
        refreshToken = "zxy",
        expiresIn = 1L
    )

    private lateinit var interactorSUT: BaseInteractorMock
    private var testConfig = UnitTestSDKConfig(
        context = applicationContext,
        authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")
    )

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(testConfig)
        testConfig.requireSDKAuthentication = false

        interactorSUT = BaseInteractorMock(
            credentialsManager,
            apiTemplate,
            context
        )

        interactorSUT.result = Resource.Success(newCredentials)
    }

    @After
    fun tearDown() {
        KarhooSDKConfigurationProvider.setConfig(
            configuration = UnitTestSDKConfig(
                context =
                applicationContext, authenticationMethod = AuthenticationMethod.KarhooUser()
            )
        )
    }

    @Test
    fun `When there are no credentials set and the Authentication Method is of type token and we have a refresh token, then the auth request is called with the new credentials`() {

        whenever(credentialsManager.credentials).thenReturn(credentials)
        whenever(
            apiTemplate.authRefresh(
                mapOf(
                    Pair("client_id", "clientId"),
                    Pair("refresh_token", "zxy"),
                    Pair("grant_type", "refresh_token")
                )
            )
        ).thenReturn(CompletableDeferred(Resource.Success(newCredentials)))

        interactorSUT.execute {
            Assert.assertTrue(it is Resource.Success)
            Assert.assertEquals(((it as Resource.Success).data as Credentials).accessToken, newCredentials.accessToken)
            Assert.assertEquals((it.data as Credentials).expiresIn, newCredentials.expiresIn)
            Assert.assertEquals((it.data as Credentials).refreshExpiresIn, newCredentials.refreshExpiresIn)
            Assert.assertEquals((it.data as Credentials).refreshToken, newCredentials.refreshToken)
        }
    }

    @Test
    fun `When there are no credentials set and the Authentication Method is of type token and we have a refresh token and the auth request fails, then an error result is sent back`() {

        whenever(credentialsManager.credentials).thenReturn(credentials)
        whenever(
            apiTemplate.authRefresh(
                mapOf(
                    Pair("client_id", "clientId"),
                    Pair("refresh_token", "zxy"),
                    Pair("grant_type", "refresh_token")
                )
            )
        ).thenReturn(CompletableDeferred(Resource.Failure(KarhooError.InternalSDKError)))

        interactorSUT.execute {
            Assert.assertTrue(it is Resource.Failure)
            Assert.assertEquals((it as Resource.Failure).error, KarhooError.InternalSDKError)
        }

        Assert.assertTrue(true)

    }

    @Test
    fun `When there is a credentials set and the Authentication Method is of type token and we don't have a refresh token, then the requireSDKAuthentication is called`() {
        whenever(credentialsManager.credentials).thenReturn(credentialsWithoutRefresh)
        whenever(
            apiTemplate.authRefresh(
                mapOf(
                    Pair("client_id", "clientId"),
                    Pair("refresh_token", "zxy"),
                    Pair("grant_type", "refresh_token")
                )
            )
        ).thenReturn(CompletableDeferred(Resource.Success(newCredentials)))
        testConfig.testCredentials = newCredentials

        interactorSUT.execute {
            Assert.assertTrue(it is Resource.Success)
            Assert.assertTrue(testConfig.requireSDKAuthentication)
        }
    }

    @Test
    fun `When there is a credentials set and the Authentication Method is of type token and we don't have a refresh expiration time, then the requireSDKAuthentication is called`() {
        whenever(credentialsManager.credentials).thenReturn(credentialsWithoutRefresh)
        whenever(
            apiTemplate.authRefresh(
                mapOf(
                    Pair("client_id", "clientId"),
                    Pair("refresh_token", "zxy"),
                    Pair("grant_type", "refresh_token")
                )
            )
        ).thenReturn(CompletableDeferred(Resource.Success(newCredentials)))
        testConfig.testCredentials = newCredentials

        interactorSUT.execute {
            Assert.assertTrue(it is Resource.Success)
            Assert.assertTrue(testConfig.requireSDKAuthentication)
        }
    }

    @Test
    fun `When there are no credentials set and the Authentication Method is of type token and we don't have a refresh token, then the credentialsManager stores the new data`() {
        whenever(credentialsManager.credentials).thenReturn(credentialsWithoutRefreshExpirationTime)
        whenever(
            apiTemplate.authRefresh(
                mapOf(
                    Pair("client_id", "clientId"),
                    Pair("refresh_token", "zxy"),
                    Pair("grant_type", "refresh_token")
                )
            )
        ).thenReturn(CompletableDeferred(Resource.Success(newCredentials)))
        testConfig.testCredentials = newCredentials

        interactorSUT.execute {
            Assert.assertTrue(it is Resource.Success)
            Assert.assertTrue(testConfig.requireSDKAuthentication)
        }
    }
}
