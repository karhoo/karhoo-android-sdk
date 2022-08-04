package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.VehicleMappings
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class QuoteImagesRuleListIntegrationTest {
    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   We need to retrieve the quote images rule list
     * When:    A successful response has been returned
     * Then:    The response payload should contain more than 0 rules
     **/
    @Test
    fun quoteImageRuleListTest() {
        serverRobot {
            quoteImagesRuleListResponse(code = HttpURLConnection.HTTP_OK, response = ServerRobot.QUOTE_IMAGES_RULE_LIST, endpoint = "https://cdn.karhoo.com/s/images/vehicles/config.json")
        }

        var result: VehicleMappings? = null
        KarhooApi.quotesService.getVehicleMappings().execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()

                }
                is Resource.Failure -> {
                    result = null
                    latch.countDown()
                }
            }
        }

        latch.await()
        Assertions.assertThat(result?.mappings?.size).isGreaterThan(0)
    }

    companion object {
        private const val IMAGES_CND_URL = "test"
    }
}