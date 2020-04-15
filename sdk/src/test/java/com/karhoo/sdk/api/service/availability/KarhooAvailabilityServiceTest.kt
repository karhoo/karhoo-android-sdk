package com.karhoo.sdk.api.service.availability

import com.karhoo.sdk.api.model.QuotesSearch
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooAvailabilityServiceTest {

    private var quotesSearch: QuotesSearch = mock()
    private var availabilityInteractor: AvailabilityInteractor = mock()

    @InjectMocks
    private lateinit var service: KarhooAvailabilityService

    /**
     * Given:   A request is made to get a call
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create availability interactor when getting availability`() {
        val availabilitiesCall = service.availability(quotesSearch)
        Assert.assertNotNull(availabilitiesCall)

    }
}