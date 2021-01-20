package com.karhoo.sdk.api.service.loyalty

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertNotNull
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
     * Given: A request is made to get a loyalty balance for a user
     * When: The call is constructed and executed
     * Then: A call should be made to the appropriate endpoint
     */
    @Test
    fun `loyalty balance call is created when looking for balance`() {
        val call = service.getBalance("1234")
        assertNotNull(call)
    }

    /**
     * Given: A request is made to get a loyalty conversion for a loyalty balance
     * When: The call is constructed and executed
     * Then: A call should be made to the appropriate endpoint
     */
    @Test
    fun `loyalty conversion call is created when looking for conversion rates on balance`() {
        val call = service.getConversionRates("1234")
        assertNotNull(call)
    }
}