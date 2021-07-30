package com.karhoo.sdk.api.service.config

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.UIConfig
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.config.ui.UIConfigProvider
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class UIConfigInteractorTest : BaseKarhooUserInteractorTest() {

    private val uiConfigProvider: UIConfigProvider = mock()
    private val userManager: UserManager = mock()
    private val userInfo: UserInfo = mock()
    private val organisation: Organisation = mock()

    private val uiConfigRequest = UIConfigRequest("viewId")
    private val uiConfig = UIConfig(true)

    private lateinit var uiConfigInteractor: UIConfigInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        uiConfigInteractor = UIConfigInteractor(credentialsManager, apiTemplate, uiConfigProvider, userManager, context)
    }

    /**
     * Given:   The UIConfigRequest is set
     * When:    A request is made to get the config for a view
     * Then:    The config for that view should be returned
     */
    @Test
    fun `uiConfigRequest set returns the config for the view`() {
        whenever(uiConfigProvider.fetchConfig(any(), any()))
                .thenReturn(CompletableDeferred(Resource.Success(data = uiConfig)))
        whenever(userManager.user).thenReturn(userInfo)
        whenever(userInfo.organisations).thenReturn(listOf(organisation))
        whenever(organisation.id).thenReturn("JOHNCABS")

        uiConfigInteractor.uiConfigRequest = uiConfigRequest

        var returnedUIConfig: UIConfig? = null
        runBlocking {
            uiConfigInteractor.execute {
                when (it) {
                    is Resource.Success -> returnedUIConfig = it.data
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedUIConfig)
        assertEquals(returnedUIConfig!!, uiConfig)
    }

    /**
     * Given:   The UIConfig is set
     * When:    The user org doesnt exist
     * Then:    An internal SDK error should be returned
     */
    @Test
    fun `no user org set returns an internal error`() {
        whenever(userManager.user).thenReturn(userInfo)
        whenever(userInfo.organisations).thenReturn(listOf(organisation))
        whenever(organisation.id).thenReturn("")

        uiConfigInteractor.uiConfigRequest = uiConfigRequest

        var shouldBeNull: UIConfig? = null
        var error: KarhooError? = null

        runBlocking {
            uiConfigInteractor.execute {
                when (it) {
                    is Resource.Success -> shouldBeNull = it.data
                    is Resource.Failure -> error = it.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
    }

    /**
     * Given:   The UIConfigRequest isn't set
     * When:    A request is made to get a config for a view
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `no uiConfigRequest set returns an internal error`() {
        var shouldBeNull: UIConfig? = null
        var error: KarhooError? = null

        runBlocking {
            uiConfigInteractor.execute {
                when (it) {
                    is Resource.Success -> shouldBeNull = it.data
                    is Resource.Failure -> error = it.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
    }

}
