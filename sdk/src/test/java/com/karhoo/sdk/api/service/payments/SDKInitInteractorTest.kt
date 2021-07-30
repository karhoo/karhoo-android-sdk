package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.SDKInitRequest
import com.karhoo.sdk.api.network.response.Resource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class SDKInitInteractorTest {

    private var credentialsManager: CredentialsManager = mock()
    private var apiTemplate: APITemplate = mock()
    private val context: CoroutineContext = Unconfined

    private lateinit var interactor: SDKInitInteractor

    @Before
    fun setUp() {
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = SDKInitInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get a token
     * When:    The request is finished
     * Then:    A sdk initialiser object should be returned
     */
    @Test
    fun `requesting an SDK Initialiser returns a valid object`() {
        whenever(apiTemplate.sdkInitToken("organisation_id", "GBP"))
                .thenReturn(CompletableDeferred(Resource.Success(BraintreeSDKToken(token = "12345678"))))

        runBlocking {
            interactor.sdkInitRequest = SDKInitRequest("organisation_id", "GBP")
            interactor.execute {}
        }

        verify(apiTemplate).sdkInitToken("organisation_id", "GBP")
    }
}
