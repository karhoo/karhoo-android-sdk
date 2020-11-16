package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test

class VerifyQuoteInteractorTest : BaseKarhooUserInteractorTest() {

    internal lateinit var interactor: VerifyQuoteInteractor

    private val id = ""

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = VerifyQuoteInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to check fleet coverage of an area
     * When:    There is coverage in the area
     * Then:    A successful response is returned
     **/
    @Test
    fun checkVerifyQuoteSuccessResponse() {

    }

    /**
     * Given:   A request is made to check fleet coverage of an area
     * When:    There is no coverage in the area
     * Then:    A failure response is returned
     **/
    @Test
    fun checkVerifyQuoteFailureResponse() {

    }
}