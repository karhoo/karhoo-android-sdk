package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.response.Resource
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
import org.mockito.Mockito.verify

class ReverseGeolocateInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: ReverseGeolocateInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = ReverseGeolocateInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   a valid lat lng
     * When:    interactor creates request
     * Then:    reverse geocode called through API
     */
    @Test
    fun `requesting reverse geo location returns locationInfo`() {
        val latitude = 51.5128
        val longitude = -0.1289
        val position = Position(latitude, longitude)
        val locationInfo = LocationInfo(position = position)
        whenever(apiTemplate.reverseGeocode(latitude, longitude))
                .thenReturn(CompletableDeferred(Resource.Success(locationInfo)))
        interactor.position = position

        var returnedLocationInfo: LocationInfo? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedLocationInfo = result.data
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }

        assertNotNull(returnedLocationInfo)
        assertEquals(locationInfo, returnedLocationInfo)
        verify(apiTemplate).reverseGeocode(latitude, longitude)
    }

    /**
     * Given:   The lat long isn't set
     * When:    A request is made to get details of a place via lat long
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `reverse geolocate when lat long not set returns an error`() {
        var shouldBeNull: LocationInfo? = null
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