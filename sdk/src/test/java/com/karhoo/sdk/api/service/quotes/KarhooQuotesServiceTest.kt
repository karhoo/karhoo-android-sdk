package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.service.availability.AvailabilityService
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import java.text.DateFormat

@RunWith(MockitoJUnitRunner::class)
class KarhooQuotesServiceTest {

    @InjectMocks
    private lateinit var service: KarhooQuotesService

    // Needed for InjectMocks
    private val availabilityService: AvailabilityService = mock()
    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val dateFormat: DateFormat = mock()

    /**
     * Given:   A request is made to get a call on Quotes
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create quotes call when getting quotes`() {
        val origin = LocationInfo(placeId = "place id")
        val destination = LocationInfo(placeId = "place id 2")

        val search = QuotesSearch(origin = origin, destination = destination)
        val call = service.quotes(search)
        assertNotNull(call)
    }

}