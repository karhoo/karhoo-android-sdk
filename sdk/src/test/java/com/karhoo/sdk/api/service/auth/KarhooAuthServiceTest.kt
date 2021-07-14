package com.karhoo.sdk.api.service.auth

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.datastore.user.UserStore
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.service.payments.PaymentsService
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooAuthServiceTest {

    // Needed for InjectMocks
    private val credentialsManager: CredentialsManager = mock()
    private val userManager: UserManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val userStore: UserStore = mock()
    private val paymentsservice: PaymentsService = mock()

    @InjectMocks
    private lateinit var service: KarhooAuthService

    /**
     * When:   A request is made to get an auth token
     * Then:   A call should be returned to the appropriate endpoint
     */
    @Test
    fun `create auth service to do a token login`() {
        val tokenLoginCall = service.login(token = "123")
        assertNotNull(tokenLoginCall)
    }

    /**
     * When:   A request is made to get user info with credentials
     * Then:   A call should be returned to the appropriate endpoint
     */
    @Test
    fun `Create an auth service retrieve user info with credentials`() {
        val credentialsLoginCall = service.login(Credentials(accessToken = "123456", refreshToken = "zxy", expiresIn = 1L))
        assertNotNull(credentialsLoginCall)
    }

    /**
     * When:   A request is made to revoke an auth token
     * Then:   A call should be returned to the appropriate endpoint
     */
    @Test
    fun `create auth service to do revoke token`() {
        val revokeTokenCall = service.revoke()
        assertTrue(revokeTokenCall is AuthRevokeInteractor)
    }
}