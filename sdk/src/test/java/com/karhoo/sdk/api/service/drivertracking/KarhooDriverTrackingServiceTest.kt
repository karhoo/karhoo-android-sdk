package com.karhoo.sdk.api.service.drivertracking

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooDriverTrackingServiceTest {

    // Needed for InjectMocks
    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()

    @InjectMocks
    private lateinit var service: KarhooDriverTrackingService

    /**
     * Given:   A request is made to track a driver
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create track driver call when tracking driver`() {
        val call = service.trackDriver("1234")
        Assert.assertNotNull(call)
    }

}
