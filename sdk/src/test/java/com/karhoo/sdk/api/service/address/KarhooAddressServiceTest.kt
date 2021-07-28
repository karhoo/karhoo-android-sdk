package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LocationInfoRequest
import com.karhoo.sdk.api.network.request.PlaceSearch
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooAddressServiceTest {

    private var placeSearch: PlaceSearch = mock()
    private var position: Position = mock()

    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()

    @InjectMocks
    private lateinit var service: KarhooAddressService

    /**
     * Given:   A request is made to get a call on address lookup
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create address lookup call when getting addresses`() {
        val locationCall = service.placeSearch(placeSearch)
        assertNotNull(locationCall)
    }

    /**
     * Given:   A request is made to get a call on address lookup
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create reverse geo locate call when getting addresses`() {
        val locationCall = service.reverseGeocode(position)
        assertNotNull(locationCall)
    }

    /**
     * Given:   A request is made to get a call on location details
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create location details call when getting addresses`() {
        val locationCall = service.locationInfo(LocationInfoRequest("12345678", "a_session_token"))
        assertNotNull(locationCall)
    }

}
