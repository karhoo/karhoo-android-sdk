package com.karhoo.sdk.api.service.availability

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.network.request.AvailabilityRequest
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
import java.util.Date

class AvailabilityInteractorTest : BaseKarhooUserInteractorTest() {

    private val origin = LocationInfo(
            placeId = "123",
            position = Position(0.5, 0.7))

    private val destination = LocationInfo(
            placeId = "321",
            position = Position(1.1, 2.3))

    private val dateScheduled = Date(0)
    private val availabilitySearch = QuotesSearch(
            origin = origin,
            destination = destination,
            dateScheduled = dateScheduled)
    private val availabilityRequest = AvailabilityRequest(origin.placeId, destination.placeId, dateScheduled)
    private val availabilities = Categories(listOf("cat1", "cat2", "cat3"))

    private lateinit var availabilityInteractor: AvailabilityInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        availabilityInteractor = AvailabilityInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   a valid QuotesSearch
     * When:    createRequest for availabilities
     * Then:    return availabilities
     */
    @Test
    fun `requesting availability returns updates`() {
        whenever(apiTemplate.availabilities(availabilityRequest))
                .thenReturn(CompletableDeferred(Resource.Success(data = availabilities)))
        availabilityInteractor.quotesSearch = availabilitySearch

        var returnedCategories: Categories? = null
        runBlocking {
            availabilityInteractor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedCategories = result.data
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }
        assertNotNull(returnedCategories)
        assertEquals(returnedCategories!!, availabilities)
    }

    /**
     * Given:   The search isn't set
     * When:    A request is made to get a list of addresses
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `autocomplete query not set returns an error`() {
        var shouldBeNull: Categories? = null
        var error: KarhooError? = null

        runBlocking {
            availabilityInteractor.execute { result ->
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
