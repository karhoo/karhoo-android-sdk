package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.LoyaltyNonce
import com.karhoo.sdk.api.network.request.LoyaltyPreAuthPayload
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

class LoyaltyPreAuthInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: LoyaltyPreAuthInteractor
    companion object {
        private const val LOYALTY_ID = "12"
    }

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = LoyaltyPreAuthInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid loyalty id
     * When:    When requesting loyalty pre auth
     * Then:    A status should be returned
     */
    @Test
    fun `requesting pre auth returns pre auth`() {

        val loyaltyNonce = LoyaltyNonce("123")
        var loyaltyPreAuth = LoyaltyPreAuthPayload("EUR", 100, true, "test")

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope"))
                                                )
        whenever(apiTemplate.postLoyaltyPreAuth(LOYALTY_ID, loyaltyPreAuth)).thenReturn(CompletableDeferred(Resource.Success(loyaltyNonce)))

        interactor.loyaltyId = LOYALTY_ID
        interactor.preAuthRequest = loyaltyPreAuth

        var returnedLoyaltyNonce: LoyaltyNonce? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedLoyaltyNonce = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        junit.framework.Assert.assertNotNull(returnedLoyaltyNonce)
        junit.framework.Assert.assertEquals(loyaltyNonce, returnedLoyaltyNonce)
        verify(apiTemplate).postLoyaltyPreAuth(LOYALTY_ID, loyaltyPreAuth)
    }

    /**
     * Given:   The loyalty programme id isn't set
     * When:    A request is made to fetch status
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `loyalty can earn null when no loyalty Id is passed`() {
        var shouldBeNull: LoyaltyNonce? = null
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
