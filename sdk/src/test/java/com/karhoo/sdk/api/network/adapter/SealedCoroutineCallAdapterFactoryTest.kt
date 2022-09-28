//package com.karhoo.sdk.api.network.adapter
//
//import com.github.tomakehurst.wiremock.junit.WireMockRule
//import com.google.gson.GsonBuilder
//import com.karhoo.sdk.analytics.Analytics
//import com.karhoo.sdk.analytics.Event
//import com.karhoo.sdk.analytics.Payloader
//import com.karhoo.sdk.api.SDKTestConfig
//import com.karhoo.sdk.api.network.client.APITemplate
//import com.karhoo.sdk.api.network.client.APITemplate.Companion.BOOKING_WITH_NONCE_METHOD
//import com.karhoo.sdk.api.network.request.TripBooking
//import com.karhoo.sdk.api.pairCaptor
//import com.nhaarman.mockitokotlin2.mock
//import com.nhaarman.mockitokotlin2.verify
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.junit.MockitoJUnitRunner
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//@RunWith(MockitoJUnitRunner::class)
//class SealedCoroutineCallAdapterFactoryTest {
//
//    @get:Rule
//    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)
//
//    private var analytics: Analytics = mock()
//    private var tripBooking: TripBooking = mock()
//    private lateinit var retrofit: Retrofit
//    private lateinit var apiTemplate: APITemplate
//
//    @Before
//    fun setup() {
//
//        retrofit = Retrofit.Builder()
//            .baseUrl(SDKTestConfig.REST_API_LINK)
//            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
//            .addCallAdapterFactory(SealedCoroutineCallAdapterFactory(analytics))
//            .build()
//        apiTemplate = retrofit.create(APITemplate::class.java)
//    }
//
//    @After
//    fun teardown() {
//        wireMockRule.resetAll()
//    }
//
//    /**
//     * Given:   a request is made
//     * When:    a failure occurs where the response type is not an instance of Response
//     * Then:    a request error analytics event should be fired
//     */
//    @Test
//    fun `a call failure for not a Response raw type triggers a request error analytics event`() {
//        runBlocking {
//            apiTemplate.bookWithNonce(tripBooking).await()
//
//            val (event, payloader) = pairCaptor<Event, Payloader>()
//
//            verify(analytics).fireEvent(event.capture(), payloader.capture())
//
//            assertEquals(event.firstValue, Event.REQUEST_ERROR)
//            assertEquals(
//                payloader.firstValue.payload[Event.REQUEST_ERROR.value],
//                "Failed to connect to /127.0.0.1:80"
//            )
//            assertEquals(
//                payloader.firstValue.payload["request_url"],
//                "${SDKTestConfig.REST_API_LINK}${BOOKING_WITH_NONCE_METHOD}"
//            )
//        }
//    }
//}
