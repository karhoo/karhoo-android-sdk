package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.Places
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.request.PlaceSearch
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
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

class PlacesInteractorTest : BaseKarhooUserInteractorTest() {

    internal lateinit var interactor: PlacesInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = PlacesInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get a list of addresses
     * When:    The request is executed
     * Then:    A call should be made to the AddressAutocomplete endpoint
     **/
    @Test
    fun `requesting places returns Places`() {
        val placesResponse = Places()
        whenever(apiTemplate.placeSearch(any()))
                .thenReturn(CompletableDeferred(Resource.Success(placesResponse)))

        interactor.placeSearch = PlaceSearch(
                position = Position(
                        latitude = 0.4,
                        longitude = 0.3),
                query = "qqw",
                sessionToken = "123")

        var places: Places? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> places = result.data
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }

        assertNotNull(places)
        assertEquals(placesResponse, places!!)
        verify(apiTemplate).placeSearch(any())
    }

    /**
     * Given:   The search isn't set
     * When:    A request is made to get a list of addresses
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `autocomplete query not set returns an error`() {
        var shouldBeNull: Places? = null
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
}
