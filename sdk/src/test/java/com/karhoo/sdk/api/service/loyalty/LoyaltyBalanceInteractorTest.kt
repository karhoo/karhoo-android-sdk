package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoyaltyBalanceInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: LoyaltyBalanceInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = LoyaltyBalanceInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid loyalty id
     * When:    When requesting loyalty balance
     * Then:    A balance should be returned
     */
    @Test
    fun `requesting loyalty balance returns balance`() {
        val loyaltyBalance = LoyaltyBalance(123, burnable = true)

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
        whenever(apiTemplate.getLoyaltyBalance(LOYALTY_ID)).thenReturn(CompletableDeferred(Resource.Success(loyaltyBalance)))

        interactor.loyaltyId = LOYALTY_ID
        var returnedLoyaltyBalance: LoyaltyBalance? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedLoyaltyBalance = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedLoyaltyBalance)
        assertEquals(loyaltyBalance, returnedLoyaltyBalance)
        verify(apiTemplate).getLoyaltyBalance(LOYALTY_ID)
    }

    /**
     * Given:   The loyalty programme id isn't set
     * When:    A request is made to fetch balance
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `loyalty balance null when no loyalty Id is passed`() {
        var shouldBeNull: LoyaltyBalance? = null
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

    companion object {
        private const val LOYALTY_ID = "1234"
    }

}