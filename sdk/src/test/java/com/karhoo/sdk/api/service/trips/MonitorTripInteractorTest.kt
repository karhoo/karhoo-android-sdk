package com.karhoo.sdk.api.service.trips

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.FleetInfo
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class MonitorTripInteractorTest : BaseKarhooUserInteractorTest() {

    private val fleetInfo: FleetInfo = FleetInfo(
            fleetId = FLEET_ID,
            description = DESCRIPTION,
            logoUrl = LOGO_URL,
            name = NAME,
            phoneNumber = PHONE_NUMBER
                                                )
    private val tripInfo: TripInfo = TripInfo(
            tripId = TRIP_IDENTIFIER,
            fleetInfo = fleetInfo
                                             )

    private lateinit var interactor: MonitorTripInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = MonitorTripInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid trip id
     * When:    When requesting to listen to a trip
     * And:
     * Then:    The trip should be updated through the observer with any changes
     */
    @Test
    fun `listening to a guest booking trip returns updates`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.Guest("identifier", "referer", "organisationId")))
        whenever(apiTemplate.guestTripDetails(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(tripInfo)))

        interactor.tripIdentifier = TRIP_IDENTIFIER
        var returnedTripId: String? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedTripId = result.data.tripId
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }

        assertEquals(TRIP_IDENTIFIER, returnedTripId)
    }

    /**
     * Given:   A valid trip id
     * When:    When requesting to listen to a trip
     * Then:    The trip should be updated through the observer with any changes
     */
    @Test
    fun `listening to a trip returns updates`() {
        whenever(apiTemplate.tripDetails(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(tripInfo)))

        interactor.tripIdentifier = TRIP_IDENTIFIER
        var returnedTripId: String? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedTripId = result.data.tripId
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }

        assertEquals(TRIP_IDENTIFIER, returnedTripId)
    }

    /**
     * Given:   A valid trip response
     * When:    Parsing the gRPC to SDK models
     * Then:    Null values should be taken into consideration
     */
    @Test
    fun `null values from trip do not throw NPE`() {
        whenever(apiTemplate.tripDetails(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(tripInfo)))

        interactor.tripIdentifier = TRIP_IDENTIFIER
        var returnedFleetInfo: FleetInfo? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedFleetInfo = result.data.fleetInfo
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }

        assertEquals(fleetInfo, returnedFleetInfo)
    }

    /**
     * Given:   The trip id isn't set
     * When:    A request is made to monitor a trip
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `monitor trip when id not set returns an error`() {
        var shouldBeNull: TripInfo? = null
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
        private const val TRIP_IDENTIFIER = "1234"
        private const val FLEET_ID = "John's cabs"
        private const val DESCRIPTION = "Cars"
        private const val LOGO_URL = "www.google.com"
        private const val NAME = "John Smith"
        private const val PHONE_NUMBER = "08080808"
    }

}