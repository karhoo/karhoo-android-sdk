package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.LoyaltyStatus
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoyaltyStatusInteractorTest : BaseKarhooUserInteractorTest()  {

    private lateinit var interactor: LoyaltyStatusInteractor
    companion object {
        private const val LOYALTY_ID = "12"
    }

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = LoyaltyStatusInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid loyalty id
     * When:    When requesting loyalty status
     * Then:    A status should be returned
     */
    @Test
    fun `requesting loyalty status returns status`() {
        val loyaltyStatus = LoyaltyStatus(11, canBurn = true, canEarn = true)

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope"))
                                                )
        whenever(apiTemplate.loyaltyStatus(LOYALTY_ID)).thenReturn(CompletableDeferred(Resource.Success(loyaltyStatus)))

        interactor.loyaltyId = LOYALTY_ID
        var returnedLoyaltyStatus: LoyaltyStatus? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedLoyaltyStatus = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        junit.framework.Assert.assertNotNull(returnedLoyaltyStatus)
        junit.framework.Assert.assertEquals(loyaltyStatus, returnedLoyaltyStatus)
        verify(apiTemplate).loyaltyStatus(LOYALTY_ID)
    }

    /**
     * Given:   The loyalty programme id isn't set
     * When:    A request is made to fetch status
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `loyalty balance null when no loyalty Id is passed`() {
        var shouldBeNull: LoyaltyStatus? = null
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

        Assert.assertEquals(KarhooError.InternalSDKError, error)
        Assert.assertNull(shouldBeNull)
    }
}
