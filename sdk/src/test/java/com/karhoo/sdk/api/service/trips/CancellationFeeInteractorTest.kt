package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.BookingFee
import com.karhoo.sdk.api.model.BookingFeePrice
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
import org.mockito.ArgumentMatchers

class CancellationFeeInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: CancellationFeeInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = CancellationFeeInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid trip id
     * When:    When requesting to cancel a trip
     * Then:    The cancellation fee should come back with true
     */
    @Test
    fun `cancellation to a trip returns true`() {
        val bookingFee = BookingFee(true)

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
        whenever(apiTemplate.cancellationFee(FEE_IDENTIFIER)).thenReturn(CompletableDeferred
                                                                         (Resource.Success(bookingFee)))

        interactor.feeIdentifier = FEE_IDENTIFIER
        var returnedBookingFee: BookingFee? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedBookingFee = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedBookingFee)
        assertEquals(bookingFee, returnedBookingFee)
        verify(apiTemplate).cancellationFee(FEE_IDENTIFIER)
    }

    /**
     * Given:   A valid trip id
     * When:    When requesting to cancel a trip
     * Then:    The cancellation fee should be returned as true
     */
    @Test
    fun `cancelling a trip returns a fee`() {
        val bookingFeePrice = BookingFeePrice("GBP", "", 500)
        val bookingFee = BookingFee(true, bookingFeePrice)
        whenever(apiTemplate.cancellationFee(ArgumentMatchers.anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(bookingFee)))

        interactor.feeIdentifier = FEE_IDENTIFIER
        var returnedBookingFee: BookingFee? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedBookingFee = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedBookingFee)
        assertEquals(bookingFee, returnedBookingFee)
        verify(apiTemplate).cancellationFee(FEE_IDENTIFIER)
    }

    /**
     * Given:   The trip id isn't set
     * When:    A request is made to cancel a trip
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `cancellation fee for a trip when id not set returns an error`() {
        var shouldBeNull: BookingFee? = null
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
        private const val FEE_IDENTIFIER = "1234"
    }

}