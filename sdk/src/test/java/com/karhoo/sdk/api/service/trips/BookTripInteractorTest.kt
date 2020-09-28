package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.FlightDetails
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.network.request.PassengerDetails
import com.karhoo.sdk.api.network.request.Passengers
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class BookTripInteractorTest : BaseKarhooUserInteractorTest() {

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

    private val nonceId = "ABCD123"

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = BookTripInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to make a booking with Nonce
     * When:    The request is executed
     * Then:    The endpoint called is the correct one
     **/
    @Test
    fun `creating a booking with nonce results in the use of the correct endpoint`() {
        interactor.tripBooking = TripBooking(
                nonce = nonceId,
                quoteId = quoteId,
                passengers = Passengers(
                        passengerDetails = listOf(passengerDetails),
                        additionalPassengers = 0))

        runBlocking {
            interactor.execute {}
            delay(5)
        }

        verify(apiTemplate).bookWithNonce(any())
    }

    /**
     * Given:   A request is made to make a booking with a blank nonce
     * When:    The request is executed
     * Then:    The endpoint called is the correct one
     **/
    @Test
    fun `creating a booking with a blank nonce results in the use of the correct endpoint`() {
        interactor.tripBooking = TripBooking(
                nonce = "",
                quoteId = quoteId,
                passengers = Passengers(
                        passengerDetails = listOf(passengerDetails),
                        additionalPassengers = 0))
        runBlocking {
            interactor.execute {}
            delay(5)
        }

        verify(apiTemplate).book()
    }

    /**
     * Given:   A request is made to make a booking with a null nonce
     * When:    The request is executed
     * Then:    The endpoint called is the correct one
     **/
    @Test
    fun `creating a booking with a null nonce results in the use of the correct endpoint`() {
        interactor.tripBooking = TripBooking(
                nonce = null,
                quoteId = quoteId,
                passengers = Passengers(
                        passengerDetails = listOf(passengerDetails),
                        additionalPassengers = 0))
        runBlocking {
            interactor.execute {}
            delay(5)
        }

        verify(apiTemplate).book()
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

        verify(apiTemplate).bookWithNonce(request)
    }

}