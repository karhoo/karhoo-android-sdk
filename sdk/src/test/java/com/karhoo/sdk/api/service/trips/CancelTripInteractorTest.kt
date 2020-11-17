package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.CancellationReason
import com.karhoo.sdk.api.network.adapter.void
import com.karhoo.sdk.api.network.request.CancellationRequest
import com.karhoo.sdk.api.network.request.TripCancellation
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
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

class CancelTripInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: CancelTripInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = CancelTripInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid trip id
     * When:    When requesting to cancel to a trip
     * Then:    The trip should be cancelled
     */
    @Test
    fun `cancelling a valid trip success`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))

        whenever(apiTemplate.cancel(TRIP_ID, CancellationRequest(CancellationReason.OTHER_USER_REASON)))
                .thenReturn(CompletableDeferred(Resource.Success(void())))
        interactor.tripCancellation = TripCancellation(TRIP_ID)

        var returnedCancelResponse: Void? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedCancelResponse = result.data
                    is Resource.Failure -> fail()
                }
            }
        }

        assertNotNull(returnedCancelResponse)
    }

    /**
     * Given:   A valid trip id
     * When:    When requesting to cancel to a trip
     * And:     It is for a guest booking
     * Then:    The trip should be cancelled
     */
    @Test
    fun `cancelling a valid guest booking trip success`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.Guest("identifier", "referer", "organisationId")))
        whenever(apiTemplate.cancelGuestBooking(TRIP_ID, CancellationRequest(CancellationReason
                                                                                     .OTHER_USER_REASON)))
                .thenReturn(CompletableDeferred(Resource.Success(void())))
        interactor.tripCancellation = TripCancellation(TRIP_ID)

        var returnedCancelResponse: Void? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedCancelResponse = result.data
                    is Resource.Failure -> fail()
                }
            }
        }

        assertNotNull(returnedCancelResponse)
    }

    /**
     * Given:   The trip id isn't set
     * When:    A request is made to cancel a trip
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `cancel trip when id not set returns an error`() {
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

    companion object {
        private const val TRIP_ID = "1234"
    }

}