package com.karhoo.sdk.api.service.trips

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.FlightDetails
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.PassengerDetails
import com.karhoo.sdk.api.network.request.Passengers
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class BookTripInteractorTest {

    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val context: CoroutineContext = Unconfined
    private val applicationContext: Context = mock()

    private lateinit var interactor: BookTripInteractor

    private val passengerDetails = PassengerDetails(
            firstName = "John",
            lastName = "Smith",
            locale = "en_GB"
                                                   )

    private val flightDetails = FlightDetails(
            flightNumber = "12345",
            comments = null
                                             )

    private val quoteId = "1234"

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = BookTripInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to make a booking
     * When:    The request is executed
     * Then:    The values should be sent to the BE
     **/
    @Test
    fun `creating a booking sends values to the BE`() {
        interactor.tripBooking = TripBooking(
                quoteId = quoteId,
                passengers = Passengers(
                        passengerDetails = listOf(passengerDetails),
                        additionalPassengers = 0))

        runBlocking {
            interactor.execute {}
            delay(5)
        }

        verify(apiTemplate).book(any())
    }

    /**
     * Given:   The user isn't set
     * When:    A request is made to make a booking
     * Then:    An Error should be returned stating search not set
     **/
    @Test
    fun `user details not set returns an error`() {
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

    /**
     * Given:   The flight details are present
     * When:    A request is made to make a booking
     * Then:    The flight details should be present in the request
     **/
    @Test
    fun `flight details present when making a POI booking`() {
        val request = TripBooking(
                nonce = "nonce",
                passengers = Passengers(
                        passengerDetails = listOf(passengerDetails),
                        additionalPassengers = 0),
                quoteId = quoteId,
                flightNumber = flightDetails.flightNumber,
                comments = flightDetails.comments
                                 )

        interactor.tripBooking = request
        runBlocking {
            interactor.execute {}
            delay(5)
        }

        verify(apiTemplate).book(request)
    }

}