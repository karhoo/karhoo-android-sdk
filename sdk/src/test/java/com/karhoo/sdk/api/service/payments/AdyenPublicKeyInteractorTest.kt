package com.karhoo.sdk.api.service.payments

import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.adyen.AdyenPublicKey
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.base.BaseKarhooUserInteractorTest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AdyenPublicKeyInteractorTest : BaseKarhooUserInteractorTest() {

    private val adyenPublicKey: AdyenPublicKey = mock()

    private lateinit var interactor: AdyenPublicKeyInteractor

    @Before
    override fun setUp() {
        super.setUp()
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = AdyenPublicKeyInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get the Adyen public key
     * When:    The call is not successful
     * Then:    An InternalSDKError is returned
     **/
    @Test
    fun `get Adyen public key call failure returns an error`() {
        var shouldBeNull: AdyenPublicKey? = null
        var error: KarhooError? = null
        val expectedError: KarhooError = KarhooError.InternalSDKError
        whenever(apiTemplate.getAdyenPublicKey())
                .thenReturn(CompletableDeferred(Resource.Failure(expectedError)))

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        assertEquals(expectedError, error)
        assertNull(shouldBeNull)
    }

    /**
     * Given:   A request is made to get the Adyen public key
     * When:    The call is successful
     * Then:    The correct response is returned
     */
    @Test
    fun `successful get Adyen public key response returns the Adyen public key`() {
        whenever(apiTemplate.getAdyenPublicKey())
                .thenReturn(CompletableDeferred(Resource.Success(adyenPublicKey)))

        var adyenKey: AdyenPublicKey? = null

        runBlocking {
            interactor.execute {
                when (it) {
                    is Resource.Success -> adyenKey = it.data
                }
            }
            delay(20)
        }

        assertEquals(adyenPublicKey, adyenKey)
    }
}
