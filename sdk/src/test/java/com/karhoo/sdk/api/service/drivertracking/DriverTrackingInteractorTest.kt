package com.karhoo.sdk.api.service.drivertracking

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class DriverTrackingInteractorTest {

    internal var credentialsManager: CredentialsManager = mock()
    internal var apiTemplate: APITemplate = mock()
    internal var context: CoroutineContext = Unconfined

    internal lateinit var interactor: DriverTrackingInteractor

    @Before
    fun setUp() {
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = DriverTrackingInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A valid trip id
     * When:    Requesting to track driver
     * Then:    The driver should be updated through the observer with any changes
     */
    @Test
    fun `tracking driver returns updates`() {
        whenever(apiTemplate.trackDriver(anyString()))
                .thenReturn(CompletableDeferred(Resource.Success(data = createDriver())))
        interactor.tripId = "1234"

        var driverTrackingInfo: DriverTrackingInfo? = null
        interactor.execute { result ->
            when (result) {
                is Resource.Success -> driverTrackingInfo = result.data
                is Resource.Failure -> fail()
            }
        }

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
