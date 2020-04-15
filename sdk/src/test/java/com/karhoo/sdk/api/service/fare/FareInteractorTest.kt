package com.karhoo.sdk.api.service.fare

import android.content.Context
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.api.model.FareBreakdown
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class FareInteractorTest {

    private var credentialsManager: CredentialsManager = mock()
    private var apiTemplate: APITemplate = mock()
    private val context: CoroutineContext = Dispatchers.Unconfined
    private val applicationContext: Context = mock()

    private lateinit var interactor: FareInteractor

    @Before
    fun setUp() {

        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
        whenever(credentialsManager.isValidToken).thenReturn(true)
        interactor = FareInteractor(credentialsManager, apiTemplate, context)
    }

    /**
     * Given:   A request is made to get fare deets
     * When:    The request is finished
     * Then:    A fare object should be returned
     */
    @Test
    fun `successful fare details returns valid object`() {
        whenever(apiTemplate.fareDetails(any()))
                .thenReturn(CompletableDeferred(Resource.Success(FARE_COMPLETE)))

        var fareResult: Fare? = null

        runBlocking {
            interactor.tripId = "12345678"
            interactor.execute {
                when (it) {
                    is Resource.Success -> fareResult = it.data
                    is Resource.Failure -> fail("This shouldnt be called")
                }
            }
            delay(5)
        }

        assertEquals(FARE_COMPLETE, fareResult)
    }

    /**
     * Given:   A request is made to get fare deets
     * When:    No trip id is set
     * Then:    An InternalSDKError is returned
     */
    @Test
    fun `trip id not set returns internalSDKError`() {
        var shouldBeNull: Fare? = null
        var error: KarhooError? = null

        runBlocking {
            interactor.execute { result ->
                when (result) {
                    is Resource.Success -> shouldBeNull = result.data
                    is Resource.Failure -> error = result.error
                }
            }
            delay(5)
        }

        assertEquals(KarhooError.InternalSDKError, error)
        assertNull(shouldBeNull)
    }

    companion object {

        private val FARE_COMPLETE = Fare(state = "COMPLETED",
                                         breakdown = FareBreakdown(total = 300,
                                                                   currency = "GBP"))
    }

}