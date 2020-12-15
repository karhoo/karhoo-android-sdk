package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooLoyaltyServiceTest {

    // Needed for InjectMocks
    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()

    @InjectMocks
    private lateinit var service: KarhooLoyaltyService

    /**
     * Given: A request is made to get a cancellation fee for a trip
     * When: The call is constructed and executed
     * Then: A call should be made to the appropriate endpoint
     */
    @Test
    fun `cancellation fee call is created when looking for fee`() {
        val call = service.getBalance("1234")
        Assert.assertNotNull(call)
    }
}