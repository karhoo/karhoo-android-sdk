package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.UserRegistration
import com.karhoo.sdk.api.network.response.Resource
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class UserRegisterInteractorTest {

    private val userRegistration = UserRegistration(
            firstName = "John",
            lastName = "Smith",
            phoneNumber = "12345678",
            email = "name@email.com",
            password = "Password123",
            locale = "Meh"
                                                   )

    private var apiTemplate: APITemplate = mock()
    private var credentialsManager: CredentialsManager = mock()
    private val context: CoroutineContext = Unconfined

    private lateinit var userRegisterInteractor: UserRegisterInteractor

    private val credentials = Credentials(
            accessToken = "123456",
            refreshToken = "zxy",
            expiresIn = 1L)

    private val userInfo: UserInfo
        get() = UserInfo(
                firstName = "John",
                lastName = "Smith",
                email = "name@email.com",
                phoneNumber = "1234567",
                userId = "1234",
                locale = "",
                organisations = mutableListOf())

    @Before
    fun setUp() {
        userRegisterInteractor = UserRegisterInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to set up a user
     * When:    The request is executed
     * Then:    The values should be sent to the BE
     *
     */
    @Test
    fun `user is passed into a request and sent to BE`() {
        whenever(apiTemplate.register(any()))
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))
        userRegisterInteractor.userRegistration = userRegistration

        var userInfo: UserInfo? = null
        runBlocking {
            userRegisterInteractor.execute { result ->
                when (result) {
                    is Resource.Success -> userInfo = result.data
                    is Resource.Failure -> fail()
                }
            }
            delay(100)
        }

        verify(apiTemplate, atLeastOnce()).register(any())
        assertEquals("1234", userInfo?.userId)
    }

    /**
     * Given:   The user registration isn't set
     * When:    A request is made to register a user
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `user register when user registration not set returns an error`() {
        var shouldBeNull: UserInfo? = null
        var error: KarhooError? = null

        runBlocking {
            userRegisterInteractor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
    }

}