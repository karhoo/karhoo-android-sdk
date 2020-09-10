package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.network.adapter.void
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PasswordResetInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: PasswordResetInteractor

    @Before
    override fun setUp() {
        super.setUp()
        interactor = PasswordResetInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to reset a password
     * When:    The request has been executed
     * Then:    An api call should be made to reset the password
     **/
    @Test
    fun `resetting password makes an api call to reset the password`() {
        whenever(apiTemplate.passwordReset(any()))
                .thenReturn(CompletableDeferred(Resource.Success(void())))
        interactor.email = "123456567@1234.dwsjh"

        var returnedPasswordReset: Void? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedPasswordReset = result.data
                    is Resource.Failure -> org.junit.Assert.fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedPasswordReset)
        verify(apiTemplate).passwordReset(any())
    }

    /**
     * Given:   The email isn't set
     * When:    A request is made to reset a users password
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `reset password when email not set returns an error`() {
        var shouldBeNull: Void? = null
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

}