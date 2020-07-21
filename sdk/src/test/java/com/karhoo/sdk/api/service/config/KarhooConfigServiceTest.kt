package com.karhoo.sdk.api.service.config

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooConfigServiceTest {

    private var uiConfigRequest: UIConfigRequest = mock()
    // Needed for InjectMocks
    private val credentialsManager: CredentialsManager = mock()
    private val apiTemplate: APITemplate = mock()
    private val userManager: UserManager = mock()

    @InjectMocks
    private lateinit var service: KarhooConfigService

    /**
     * Given:   A request is made to the ui config service
     * When:    The call is constructed and executed
     * Then:    A call should be made to the appropriate interactor
     */
    @Test
    fun `get config for ui calls the correct interactor`() {
        val uiConfigCall = service.uiConfig(uiConfigRequest)
        assertNotNull(uiConfigCall)
    }


}