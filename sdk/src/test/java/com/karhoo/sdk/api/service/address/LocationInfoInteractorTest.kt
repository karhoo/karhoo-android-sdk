package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.network.request.LocationInfoRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LocationInfoInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: LocationInfoInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = LocationInfoInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A location with a valid plcae id
     * When:    interactor creates request
     * Then:    location details request made through API
     */
    @Test
    fun `requesting location info returns locationInfo`() {
        val locationInfoResponse = LocationInfo()
        whenever(apiTemplate.locationInfo(any()))
                .thenReturn(CompletableDeferred(Resource.Success(data = locationInfoResponse)))
        interactor.locationInfoRequest = LocationInfoRequest("1234", "a_session_token")

        var locationInfo: LocationInfo? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> locationInfo = result.data
                    is Resource.Failure -> org.junit.Assert.fail()
                }
            }
            delay(5)
        }

        assertEquals(locationInfoResponse, locationInfo!!)
    }

    /**
     * Given:   The trip id isn't set
     * When:    A request is made to track a driver of a trip
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `location details when location is not set returns an error`() {
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

        assertNull(shouldBeNull)
        assertEquals(KarhooError.InternalSDKError, error)
    }

}