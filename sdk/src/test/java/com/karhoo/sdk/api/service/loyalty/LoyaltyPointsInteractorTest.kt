package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.LoyaltyPoints
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

class LoyaltyPointsInteractorTest : BaseKarhooUserInteractorTest()  {

    private lateinit var interactor: LoyaltyPointsInteractor
    companion object {
        private const val LOYALTY_ID = "12"
    }

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = LoyaltyPointsInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid loyalty id
     * When:    When requesting loyalty can earn
     * Then:    A status should be returned
     */
    @Test
    fun `requesting can earn returns can earn`() {
        val currency : String = "EUR"
        val totalAmount : Int = 100
        val burnPoints : Int = 100
        val loyaltyPoints = LoyaltyPoints(11)

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope"))
                                                )
        whenever(apiTemplate.loyaltyPointsToEarn(LOYALTY_ID, currency, totalAmount, burnPoints)).thenReturn(CompletableDeferred(Resource.Success(loyaltyPoints)))

        interactor.loyaltyId = LOYALTY_ID
        interactor.currency = currency
        interactor.totalAmount = totalAmount
        interactor.burnPoints = burnPoints
        interactor.toEarn = true

        var returnedLoyaltyPoints: LoyaltyPoints? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedLoyaltyPoints = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        junit.framework.Assert.assertNotNull(returnedLoyaltyPoints)
        junit.framework.Assert.assertEquals(loyaltyPoints, returnedLoyaltyPoints)
        verify(apiTemplate).loyaltyPointsToEarn(LOYALTY_ID, currency, totalAmount, burnPoints)
    }

    /**
     * Given:   A valid loyalty id
     * When:    When requesting loyalty can burn
     * Then:    A status should be returned
     */
    @Test
    fun `requesting can burn returns can burn`() {
        val currency : String = "EUR"
        val amount : Int = 100
        val loyaltyPoints = LoyaltyPoints(11)

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope"))
                                                )
        whenever(apiTemplate.loyaltyBurnPoints(LOYALTY_ID, currency, amount)).thenReturn(CompletableDeferred(Resource.Success(loyaltyPoints)))

        interactor.loyaltyId = LOYALTY_ID
        interactor.currency = currency
        interactor.amount = amount
        interactor.toEarn = false

        var returnedLoyaltyPoints: LoyaltyPoints? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedLoyaltyPoints = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        junit.framework.Assert.assertNotNull(returnedLoyaltyPoints)
        junit.framework.Assert.assertEquals(loyaltyPoints, returnedLoyaltyPoints)
        verify(apiTemplate).loyaltyBurnPoints(LOYALTY_ID, currency, amount)
    }

    /**
     * Given:   The loyalty programme id isn't set
     * When:    A request is made to fetch status
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `loyalty can earn null when no loyalty Id is passed`() {
        var shouldBeNull: LoyaltyPoints? = null
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
