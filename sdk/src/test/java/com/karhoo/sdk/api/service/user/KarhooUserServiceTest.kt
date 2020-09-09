package com.karhoo.sdk.api.service.user

import android.content.Context
import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.KarhooUserStore
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.UserDetailsUpdateRequest
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.request.UserRegistration
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooUserServiceTest {

    private val applicationContext: Context = mock()

    private val userRegistration = UserRegistration(
            firstName = "John",
            lastName = "Smith",
            phoneNumber = "12345678",
            email = "name@email.com",
            password = "Password123",
            locale = "Meh"
                                                   )

    private val userDetailsUpdateRequest = UserDetailsUpdateRequest(
            userId = "11111",
            firstName = "John",
            lastName = "Smith",
            phoneNumber = "12345678",
            locale = "meh",
            avatarUrl = "n/a"
                                                                   )

    private val userStore: KarhooUserStore = mock()
    // Needed for InjectMocks
    private val credentialsManager: CredentialsManager = mock()
    private val userManager: UserManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val analytics: Analytics = mock()

    @InjectMocks
    private lateinit var service: KarhooUserService

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.KarhooUser()))
    }

    /**
     * Given:   A request is made to get a call on Login user
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create login call when logging in user`() {
        val call = service.loginUser(UserLogin("name@email.com", "123456"))
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a call on Login user
     * When:    The call is made in legacy mode
     * Then:    A runtime exception is thrown
     */
    @Test(expected = RuntimeException::class)
    fun `create runtime exception when making service calls in non legacy mode`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        service.loginUser(UserLogin("name@email.com", "123456"))
    }

    /**
     * Given:   A request is made to get a call on register
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create registration call when registering user`() {
        val call = service.register(userRegistration)
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a call on register
     * When:    The call is made in legacy mode
     * Then:    A runtime exception is thrown
     */
    @Test(expected = RuntimeException::class)
    fun `create runtime exception when registering user in non legacy mode`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        service.register(userRegistration)
    }

    /**
     * Given:   A request is made to get a call on Reset Password
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create reset password call when resetting password`() {
        val call = service.resetPassword("name@email.com")
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a call on resetting password
     * When:    The call is made in legacy mode
     * Then:    A runtime exception is thrown
     */
    @Test(expected = RuntimeException::class)
    fun `create runtime exception when resetting password in non legacy mode`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        service.resetPassword("test@test.com")
    }

    /**
     * Given:   A request is made to log out a user
     * When:    The call is made
     * Then:    The user store should be asked to remove the user
     **/
    @Test
    fun `logout user fires call to remove user from user store`() {
        service.logout()
        verify(userStore).removeCurrentUser()
    }

    /**
     * Given:   A request is made to get a call on Update User Details
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create user update details call when updating user details`() {
        val call = service.updateUserDetails(userDetailsUpdateRequest)
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a call on register
     * When:    The call is made in legacy mode
     * Then:    A runtime exception is thrown
     */
    @Test(expected = RuntimeException::class)
    fun `create runtime exception when updating user details in non legacy mode`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext, authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        service.updateUserDetails(userDetailsUpdateRequest)
    }
}