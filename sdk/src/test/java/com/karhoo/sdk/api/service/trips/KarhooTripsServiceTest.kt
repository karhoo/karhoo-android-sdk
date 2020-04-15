package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.request.TripCancellation
import com.karhoo.sdk.api.network.request.TripSearch
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooTripsServiceTest {

    private val tripHistoryRequest: TripSearch = TripSearch()
    private val tripBooking: TripBooking = TripBooking(QUOTE_ID, null, null)
    private val userInfo: UserInfo = UserInfo()

    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()

    @InjectMocks
    private lateinit var service: KarhooTripsService

    /**
     * Given:   A request is made to book a trip
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create book call when booking trip`() {
        val call = service.book(tripBooking)
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to monitor a trip
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create monitor trip call when getting trip status`() {
        val call = service.trackTrip("1234")
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to cancel a trip
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `cancel trip called when canceling trip`() {
        val call = service.cancel(TripCancellation("1234"))
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a list of trips
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `list trips called when getting trips`() {
        val call = service.search(tripHistoryRequest)
        assertNotNull(call)
    }

    /**
     * Given:   A request is made to get a trips status
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     **/
    @Test
    fun `trip status call is created when looking for trip status`() {
        val call = service.status("1234")
        assertNotNull(call)
    }

    companion object {
        private const val QUOTE_ID = "myQuoteId"
    }
}
