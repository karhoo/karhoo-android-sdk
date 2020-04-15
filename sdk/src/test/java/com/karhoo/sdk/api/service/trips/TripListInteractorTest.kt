package com.karhoo.sdk.api.service.trips

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.model.TripList
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.TripSearch
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class TripListInteractorTest {

    private var credentialsManager: CredentialsManager = mock()
    private var apiTemplate: APITemplate = mock()
    private var tripDetails: TripInfo = mock()
    private val context: CoroutineContext = Unconfined
    private val applicationContext: Context = mock()

    private var trips: List<TripInfo> = listOf(tripDetails)
    private var emptyTripList = TripList()
    private var tripHistoryRequest: TripSearch = TripSearch()

    private var tripList = TripList(bookings = trips)

    private lateinit var interactor: TripListInteractor

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = TripListInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get trips
     * When:    There are no trips available
     * Then:    The response should be converted to a empty list of trip details
     */
    @Test
    fun `successful response with no trips returns a empty list of trips`() {
        whenever(apiTemplate.tripHistory(any()))
                .thenReturn(CompletableDeferred(Resource.Success(emptyTripList)))

        interactor.tripHistory = tripHistoryRequest
        var returnedTripHistory: List<TripInfo>? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedTripHistory = result.data
                    is Resource.Failure -> Assert.fail()
                }
            }
            delay(5)
        }

        Assert.assertTrue(returnedTripHistory!!.isEmpty())
    }

    /**
     * Given:   A request is made to get trips
     * When:    There are trips available
     * Then:    The response should be converted to a list of trip details
     */
    @Test
    fun `successful response with trips returns the trip list`() {
        whenever(apiTemplate.tripHistory(any()))
                .thenReturn(CompletableDeferred(Resource.Success(tripList)))

        interactor.tripHistory = tripHistoryRequest
        var returnedTripHistory: List<TripInfo>? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedTripHistory = result.data
                    is Resource.Failure -> org.junit.Assert.fail()
                }
            }
            delay(5)
        }

        assertFalse(returnedTripHistory.isNullOrEmpty())
    }

    /**
     * Given:   The trip history request isn't set
     * When:    A request is made to get a list of a users trips
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `trip list when trip history request not set returns an error`() {
        var shouldBeNull: List<TripInfo>? = null
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