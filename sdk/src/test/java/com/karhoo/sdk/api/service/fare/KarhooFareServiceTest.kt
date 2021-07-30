package com.karhoo.sdk.api.service.fare

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooFareServiceTest {

    private val userManager: UserManager = mock()
    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()

    @InjectMocks
    private lateinit var service: KarhooFareService

    /**
     * Given:   A request is made to get a call on Fare Details
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate endpoint
     */
    @Test
    fun `create fare details call when getting fare details`() {
        val call = service.fareDetails("")
        Assert.assertNotNull(call)
    }

}
