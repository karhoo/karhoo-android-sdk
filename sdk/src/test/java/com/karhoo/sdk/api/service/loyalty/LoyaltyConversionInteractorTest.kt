package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.LoyaltyConversion
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

class LoyaltyConversionInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: LoyaltyConversionInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = LoyaltyConversionInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid loyalty id
     * When:    When requesting loyalty conversion
     * Then:    A loyalty version should be returned
     */
    @Test
    fun `requesting loyalty conversion returns version`() {
        val loyaltyConversion = LoyaltyConversion("20200312")

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
        whenever(apiTemplate.loyaltyConversionRates(LOYALTY_ID)).thenReturn(CompletableDeferred(Resource.Success(loyaltyConversion)))

        interactor.loyaltyId = LOYALTY_ID
        var returnedLoyaltyConversion: LoyaltyConversion? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedLoyaltyConversion = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedLoyaltyConversion)
        assertEquals(loyaltyConversion, returnedLoyaltyConversion)
        verify(apiTemplate).loyaltyConversionRates(LOYALTY_ID)
    }

    /**
     * Given:   The loyalty programme id isn't set
     * When:    A request is made to fetch conversion
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `loyalty conversion null when no loyalty Id is passed`() {
        var shouldBeNull: LoyaltyConversion? = null
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