package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.InteractorConstants
import com.karhoo.sdk.api.service.payments.PaymentsService
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.karhoo.sdk.call.Call
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor

class UserLoginInteractorTest : BaseKarhooUserInteractorTest() {

    private var userManager: UserManager = mock()
    private var analytics: Analytics = mock()
    private var paymentService: PaymentsService = mock()
    private var paymentProviderCall: Call<PaymentProvider> = mock()

    @Captor
    private lateinit var credentialsCaptor: ArgumentCaptor<Credentials>

    private lateinit var interactor: UserLoginInteractor

    private val userInfo: UserInfo
        get() = UserInfo(
                firstName = "John",
                lastName = "Smith",
                email = "name@email.com",
                phoneNumber = "1234567",
                userId = "123",
                locale = "",
                organisations = mutableListOf(Organisation(
                        id = "123ABC",
                        name = "Karhoo",
                        roles = mutableListOf(InteractorConstants.REQUIRED_ROLE, InteractorConstants.MOBILE_USER)
                                                          )))

    private val email = "name@email.com"

    private val userLogin = UserLogin(email = email, password = "Password123")

    private val credentials = Credentials(accessToken = "123456", refreshToken = "zxy", expiresIn = 1L)

    @Before
    override fun setUp() {
        super.setUp()
        interactor = UserLoginInteractor(credentialsManager, userManager, apiTemplate, analytics, paymentService, context)
    }

    /**
     * Given:   A user wants to log into the system
     * When:    logging in the user successfully
     * Then:    A user details object should be returned with the details of that user
     * And:     A call is made to retrieve the payment provider
     */
    @Test
    fun `login user returns a user details object on successful login`() {
        whenever(apiTemplate.login(any()))
                .thenReturn(CompletableDeferred(Resource.Success(credentials)))
        whenever(apiTemplate.userProfile())
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))
        whenever(paymentService.getPaymentProvider())
                .thenReturn(paymentProviderCall)

        interactor.userLogin = userLogin
        var returnedUserInfo: UserInfo? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedUserInfo = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(100)
        }

        assertEquals(userInfo.firstName, returnedUserInfo?.firstName)
        assertEquals(userInfo.lastName, returnedUserInfo?.lastName)
        assertEquals(userInfo.phoneNumber, returnedUserInfo?.phoneNumber)
        verify(paymentService).getPaymentProvider()
    }

    /**
     * Given:   A user wants to have their details saved for later
     * When:    Logging in the user successfully
     * Then:    A user details object should be passed to the user manager
     */
    @Test
    fun `successful user gets set in user manager`() {
        whenever(apiTemplate.login(any()))
                .thenReturn(CompletableDeferred(Resource.Success(credentials)))
        whenever(apiTemplate.userProfile())
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))
        whenever(paymentService.getPaymentProvider())
                .thenReturn(paymentProviderCall)

        interactor.userLogin = userLogin
        runBlocking {
            interactor.execute {}
            delay(10)
        }

        verify(userManager, atLeastOnce()).saveUser(userInfo)
    }

    /**
     * Given:   A user wants to have their credentials saved for later
     * When:    Logging in the user successfully
     * Then:    A credentials object should be passed to the credentials manager
     */
    @Test
    fun `successful credentials gets set in credentials manager`() {
        whenever(apiTemplate.login(any()))
                .thenReturn(CompletableDeferred(Resource.Success(credentials)))
        whenever(apiTemplate.userProfile())
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))

        doNothing().whenever(credentialsManager).saveCredentials(any(), any(), any())

        interactor.userLogin = userLogin
        runBlocking {
            interactor.execute {}
            delay(100)
        }

        verify(credentialsManager).saveCredentials(capture(credentialsCaptor), any(), any())

        assertEquals("123456", credentialsCaptor.value.accessToken)
        assertEquals("zxy", credentialsCaptor.value.refreshToken)
        assertEquals(1f, credentialsCaptor.value.expiresIn?.toFloat() ?: -1f, 0f)
    }

    /**
     * Given:   The user login isn't set
     * When:    A request is made to log in a user
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `log in user when password not set returns an error`() {
        var shouldBeNull: UserInfo? = null
        var error: KarhooError? = null

        runBlocking {
            interactor.execute { result ->
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

    /**
     * Given:   A user logs in
     * When:    There is already a valid user logged in
     * Then:    UserAlreadyLoggedIn error is returned
     */
    @Test
    fun `returns UserAlreadyLoggedIn error when already valid user`() {
        whenever(userManager.isUserStillValid).thenReturn(true)
        interactor.userLogin = userLogin

        var shouldBeNull: UserInfo? = null
        var error: KarhooError? = null

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.UserAlreadyLoggedIn, error)
        assertNull(shouldBeNull)
    }

}
