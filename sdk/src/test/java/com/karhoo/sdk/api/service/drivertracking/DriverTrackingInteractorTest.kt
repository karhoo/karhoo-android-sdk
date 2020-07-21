package com.karhoo.sdk.api.service.drivertracking

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.verify
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

class DriverTrackingInteractorTest : BaseKarhooUserInteractorTest() {

    internal lateinit var interactor: DriverTrackingInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = DriverTrackingInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid trip id and AuthenticationMode is NOT Guest
     * When:    Requesting to track driver
     * Then:    The driver should be updated through the observer with any changes
     *          AND trackDriver
     */
    @Test
    fun `tracking driver returns updates`() {
        whenever(apiTemplate.trackDriver(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(data = createDriver())))
        interactor.tripIdentifier = "1234"

        var driverTrackingInfo: DriverTrackingInfo? = null
        interactor.execute { result ->
            when (result) {
                is Resource.Success -> driverTrackingInfo = result.data
                is Resource.Failure -> fail()
            }
        }

        verify(apiTemplate).trackDriver("1234")
        assertEquals(Position(0.5, 0.4), driverTrackingInfo!!.position)
        assertEquals(5, driverTrackingInfo!!.originEta)
        assertEquals(10, driverTrackingInfo!!.destinationEta)
    }

    /**
     * Given:   A valid trip id and AuthenticationMode is Guest
     * When:    Requesting to track driver
     * Then:    The driver should be updated through the observer with any changes
     *          AND guestBookingTrackDriver should be called
     */
    @Test
    fun `tracking driver returns updates in Guest Mode`() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.Guest("Identifier", "refere", "Org")))
        whenever(apiTemplate.guestBookingTrackDriver(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(data = createDriver())))
        interactor.tripIdentifier = "1234"

        var driverTrackingInfo: DriverTrackingInfo? = null
        interactor.execute { result ->
            when (result) {
                is Resource.Success -> driverTrackingInfo = result.data
                is Resource.Failure -> fail()
            }
        }

        verify(apiTemplate).guestBookingTrackDriver("1234")
        assertEquals(Position(0.5, 0.4), driverTrackingInfo!!.position)
        assertEquals(5, driverTrackingInfo!!.originEta)
        assertEquals(10, driverTrackingInfo!!.destinationEta)
    }

    /**
     * Given:   The trip id isn't set
     * When:    A request is made to track a driver of a trip
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `driver tracking when id not set returns an error`() {
        var shouldBeNull: DriverTrackingInfo? = null
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

        assertNull(shouldBeNull)
        assertEquals(KarhooError.InternalSDKError, error)
    }

    private fun createDriver() = DriverTrackingInfo(
            position = Position(0.5, 0.4),
            originEta = 5,
            destinationEta = 10)

}
