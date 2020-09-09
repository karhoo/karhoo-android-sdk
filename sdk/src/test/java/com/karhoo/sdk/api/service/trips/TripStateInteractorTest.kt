package com.karhoo.sdk.api.service.trips

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TripStateInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: TripStateInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = TripStateInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get a trip status
     * When:    The request is made
     * Then:    A call to the status endpoint should be made
     **/
    @Test
    fun `requesting a valid trip status calls the status endpoint`() {
        interactor.tripIdentifier = TRIP_ID
        runBlocking {
            interactor.execute { }
        }

        verify(apiTemplate).status(TRIP_ID)
    }

    /**
     * Given:   The trip id isn't set
     * When:    A request is made to get a trip status
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `no set trip id returns an error`() {
        var shouldBeNull: TripState? = null
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

    companion object {

        const val TRIP_ID = "1234"

    }

}