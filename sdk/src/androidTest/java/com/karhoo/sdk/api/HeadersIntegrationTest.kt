package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.network.header.CustomHeaders
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot
import com.karhoo.sdk.api.util.ServerRobot.Companion.TRIP_REQUESTED_DETAILS
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_CREATED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class HeadersIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    fun setUp() {
        serverRobot {
            authRefreshResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.TOKEN)
            authUserResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.USER_INFO)
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   A custom request interceptor has been set when initialising the SDK
     * When:    Any request is made
     * Then:    The headers from the custom request interceptor are added to the request
     */
    @Test
    fun customRequestInterceptorHeadersAreAdded() {
        KarhooApi.customHeaders = TestCustomHeaders()

        serverRobot {
            bookingResponseWithNonce(code = HTTP_CREATED, response = TRIP_REQUESTED_DETAILS, header = Pair(header, headerval))
        }

        KarhooApi.tripService.book(TestData.BOOK_TRIP_WITH_NONCE).execute {
            when (it) {
                is Resource.Success -> latch.countDown()
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(latch.count).isZero()
    }

    /**
     * Given:   API_KEY metadata has been added to the AndroidManifest.xml
     * When:    Any request is made
     * Then:    The api key header is added to the request
     */
    @Test
    fun apiKeyFromAndroidManifestIsAddedAsHeader() {
        KarhooApi.apiKey = apiKeyValue

        serverRobot {
            bookingResponseWithNonce(code = HTTP_CREATED, response = TRIP_REQUESTED_DETAILS, header = Pair(apiKeyHeader, apiKeyValue))
        }

        KarhooApi.tripService.book(TestData.BOOK_TRIP_WITH_NONCE).execute {
            when (it) {
                is Resource.Success -> latch.countDown()
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(latch.count).isZero()
    }

    class TestCustomHeaders : CustomHeaders {
        override val headers: Map<String, String> = mapOf(header to headerval)
    }

    companion object {
        const val header = "testheader"
        const val headerval = "this is a value"

        const val apiKeyHeader = "Api-Key"
        const val apiKeyValue = "ApiTestKeyValue"
    }

}