package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserDetailsInteractorTest : BaseKarhooUserInteractorTest() {

    private val userInfo = UserInfo("John",
                                    "Smith",
                                    "name@email.com",
                                    "+44123456789",
                                    "1234",
                                    "",
                                    "",
                                    mutableListOf())

    private lateinit var interactor: UserDetailsInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = UserDetailsInteractor(apiTemplate, credentialsManager, context)
    }

    /**
     * Given:   A user wants to retrieve their details
     * When:    A user has successfully logged in to the app
     * Then:    A user details object should be returned to the user manager
     */
    @Test
    fun `return user details for authenticated user`() {
        whenever(apiTemplate.userProfile())
                .thenReturn(CompletableDeferred(Resource.Success(userInfo)))

        var returnedUserInfo: UserInfo? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedUserInfo = result.data
                    is Resource.Failure -> fail()
                }
            }
        }

        assertEquals(userInfo, returnedUserInfo!!)
    }
}
