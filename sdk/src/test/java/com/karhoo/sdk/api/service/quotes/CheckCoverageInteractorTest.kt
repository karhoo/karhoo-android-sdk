package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.Coverage
import com.karhoo.sdk.api.network.request.CoverageRequest
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

class CheckCoverageInteractorTest : BaseKarhooUserInteractorTest() {

    internal lateinit var interactor: CheckCoverageInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = CheckCoverageInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to check the coverage of an area
     * When:    The request has been executed
     * Then:    An api call should be made to check the coverage
     **/
    @Test
    fun checkingCoverageMakesAnAPICallToCheckCoverage() {
        val coverageResponse = Coverage()
        whenever(apiTemplate.checkCoverage(any()))
                .thenReturn(CompletableDeferred(Resource.Success(data = coverageResponse)))
        interactor.coverageRequest = CoverageRequest("51.5549° N", "0.1084° W", "")

        var coverageInfo: Coverage? = null

    }

    //    /**
    //     * Given:   A request is made to check fleet coverage of an area
    //     * When:    There is coverage in the area
    //     * Then:    A successful response is returned
    //     **/
    //    @Test
    //    fun  checkCoverageReturnsASuccessfulResponse() {
    //
    //    }

    /**
     * Given:   A request is made to check fleet coverage of an area
     * When:    There is no coverage in the area
     * Then:    An Internal SDK error is returned
     **/
    @Test
    fun checkCoverageReturnsASuccessfulResponse() {
        var shouldBeNull: CoverageRequest? = null
        var error: KarhooError? = null

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> result.data
                    is Resource.Failure -> result.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
    }
}