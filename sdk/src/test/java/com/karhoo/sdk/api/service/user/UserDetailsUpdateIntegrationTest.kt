package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.request.UserDetailsUpdateRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UserDetailsUpdateIntegrationTest : BaseKarhooUserInteractorTest() {

    private val userId = "1234"

    private val request = UserDetailsUpdateRequest(
            userId = userId,
            avatarUrl = "https://john.smiths.mean.bean.machine.com/avatar.png",
            firstName = "John",
            lastName = "Smith",
            locale = "",
            phoneNumber = "1234567")

    private val userInfo: UserInfo
        get() = UserInfo(
                firstName = "John",
                lastName = "Smith",
                email = "name@email.com",
                phoneNumber = "1234567",
                userId = userId,
                locale = "",
                organisations = mutableListOf())

    private val analytics: Analytics = mock()
    private val userManager: UserManager = mock()

    private lateinit var interactor: UserDetailsUpdateInteractor

    private val latch = CountDownLatch(1)

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = UserDetailsUpdateInteractor(credentialsManager, apiTemplate, analytics, userManager, context)
    }

    /**
     * Given:   A request is made to update the user's details
     * When:    The request is executed
     * Then:    The values should be sent to the BE
     */
    @Test
    fun `a user profile update request is made with valid details`() {
        whenever(apiTemplate.userProfileUpdate(any(), any()))
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))

        interactor.userDetailsUpdateRequest = request

        var updatedUserInfo: UserInfo? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> {
                        updatedUserInfo = result.data
                        latch.countDown()
                    }
                    is Resource.Failure -> {
                        fail()
                    }
                }
            }
        }
        latch.await(2, TimeUnit.SECONDS)
        verify(apiTemplate, atLeastOnce()).userProfileUpdate(any(), any())
        assertEquals(userInfo, updatedUserInfo)
    }

    @Test
    fun `a user profile update request is made without valid details`() {
        var error: KarhooError? = null

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> fail("should be null")
                    is Resource.Failure -> {
                        error = result.error
                        latch.countDown()
                    }
                }
            }
        }
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(KarhooError.InternalSDKError, error)
    }

    /**
     * Given: I want to update my user details
     * When: A user details update request is given but without a valid user id
     * Then: The request should fail with internal SDK error
     */
    @Test
    fun `a user profile update request is made with invalid userId`() {
        var error: KarhooError? = null
        interactor.userDetailsUpdateRequest = request.copy(userId = "")

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> fail("Should be null")
                    is Resource.Failure -> {
                        error = result.error
                        latch.countDown()
                    }
                }
            }
        }
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(KarhooError.InternalSDKError, error)
    }

}
