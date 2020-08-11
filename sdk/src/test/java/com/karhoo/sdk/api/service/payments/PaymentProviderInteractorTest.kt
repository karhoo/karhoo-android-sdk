package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before

class PaymentProviderInteractorTest : BaseKarhooUserInteractorTest() {

    private lateinit var interactor: PaymentProviderInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = PaymentProviderInteractor(credentialsManager, apiTemplate, context)
    }
}