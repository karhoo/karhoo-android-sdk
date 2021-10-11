package com.karhoo.sdk.api.network.header

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.ANDROID
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooHeadersTest {

    private var credentialsManager: CredentialsManager = mock()
    private var credentials: Credentials = mock()
    private var headers: Headers = KarhooHeaders(credentialsManager)

    /**
     * Given:   A request is made to retrieve token
     * When:    Adding a header to a request
     * Then:    A token should be returned from the credentials manager
     */
    @Test
    fun `requesting token gets auth token from the preferences`() {
        whenever(credentialsManager.credentials).thenReturn(credentials)
        whenever(credentials.accessToken).thenReturn("SOME_TOKEN")

        assertEquals("SOME_TOKEN", headers.authenticationToken)
    }

    /**
     * Given:   A request is made to retrieve a correlation id
     * When:    Adding a header to a request
     * Then:    A token should be returned from the credentials manager
     */
    @Test
    fun `getting a new correlation id generates a fresh uuid`() {
        val correlationId = headers.generateCorrelationId("")

        val prefix = correlationId.subSequence(0, 7)
        val daRest = correlationId.subSequence(7, correlationId.length)
        assertEquals(ANDROID.toUpperCase(), prefix)
        assertNotNull(daRest)
    }

    /**
     * Given:   A request is made to retrieve a correlation id for a quote
     * When:    Adding a header to the request
     * Then:    The quote correlation id should be saved
     */
    @Test
    fun `making a booking request uses the same id as quote`() {
        val quoteCorrelationId = headers.generateCorrelationId(APITemplate.QUOTES_REQUEST_METHOD)
        val bookingCorrelationId = headers.generateCorrelationId(APITemplate.BOOKING_WITH_NONCE_METHOD)

        assertEquals(quoteCorrelationId, bookingCorrelationId)
    }

    /**
     * Given:   A request has been made to quotes
     * When:    A second request is made to quotes
     * Then:    The Ids should be different
     */
    @Test
    fun `multiple quote requests generate different ids`() {
        val quoteCorrelationIdOne = headers.generateCorrelationId(APITemplate.QUOTES_REQUEST_METHOD)
        val quoteCorrelationIdTwo = headers.generateCorrelationId(APITemplate.QUOTES_REQUEST_METHOD)

        assertNotSame(quoteCorrelationIdOne, quoteCorrelationIdTwo)
    }
}
