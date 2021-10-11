package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.Coverage
import com.karhoo.sdk.api.network.request.CoverageRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
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

class CheckCoverageInteractorTest : BaseKarhooUserInteractorTest() {

    internal lateinit var interactor: CheckCoverageInteractor

    private val latitude = "51.532156"

    private val longitude = "0.123838"

    private val dateScheduled = ""

    private val coverageRequest = CoverageRequest(
            latitude = latitude,
            longitude = longitude,
            dateScheduled = dateScheduled
                                                 )

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = CheckCoverageInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to check fleet coverage of an area
     * When:    There is coverage in the area
     * Then:    A successful response is returned
     **/
    @Test
    fun checkCoverageReturnsASuccessfulResponse() {
        val coverageInfo = Coverage(true)
        whenever(apiTemplate.checkCoverage(latitude, longitude, dateScheduled))
                .thenReturn(CompletableDeferred(Resource.Success(coverageInfo)))
        interactor.coverageRequest = coverageRequest

        var returnedCoverageInfo: Coverage? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedCoverageInfo = result.data
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }
        assertNotNull(returnedCoverageInfo)
        assertEquals(coverageInfo, returnedCoverageInfo)
        verify(apiTemplate).checkCoverage(latitude, longitude, dateScheduled)

    }

    /**
     * Given:   A request is made to check fleet coverage of an area
     * When:    There is no coverage in the area
     * Then:    A negative response is returned
     **/
    @Test
    fun checkCoverageReturnsANegativeResponse() {
        val coverageInfo = Coverage(false)
        whenever(apiTemplate.checkCoverage(latitude, longitude, dateScheduled))
                .thenReturn(CompletableDeferred(Resource.Success(coverageInfo)))
        interactor.coverageRequest = coverageRequest

        var returnedCoverageInfo: Coverage? = null
        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> returnedCoverageInfo = result.data
                    is Resource.Failure -> fail()
                }
            }
            delay(5)
        }
        assertNotNull(returnedCoverageInfo)
        assertEquals(coverageInfo, returnedCoverageInfo)
        verify(apiTemplate).checkCoverage(latitude, longitude, dateScheduled)

    }

    /**
     * Given:   Lat long is not set
     * When:    A request is made to check coverage
     * Then:    An Internal SDK error is returned
     **/
    @Test
    fun checkCoverageReturnsAnErrorIfNoLatLongAreGiven() {
        var shouldBeNull: Coverage? = null
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
