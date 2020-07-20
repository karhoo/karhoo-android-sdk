package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.UIConfig
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.TestData.Companion.USER_INFO
import com.karhoo.sdk.api.util.preferences
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ConfigIntegrationTests {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setUp() {
        serverRobot {
            successfulToken()
        }
    }

    @After
    fun tearDown() {
        preferences {
            clearUserPreference()
        }
        wireMockRule.resetAll()
    }

    /**
     * Given:   UI Config is requested
     * When:    Successful response is returned
     * Then:    The response payload should be that of UIConfig
     */
    @Test
    fun uiConfigReturnsSuccessfully() {
        preferences {
            setUserPreference(USER_INFO)
        }

        var result: UIConfig? = null

        KarhooApi.configService.uiConfig(uiConfigRequest).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   UI Config is requested
     * When:    the UIConfig is not present
     * Then:    The response should fail
     */
    @Test
    fun uiConfigDoesntExistSoFails() {
        var result: KarhooError? = null

        KarhooApi.configService.uiConfig(uiConfigRequest.copy(viewId = "NOT A REAL VIEW")).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.NoConfigAvailableForView)
    }

    companion object {

        private val uiConfigRequest = UIConfigRequest("additionalFeedbackView")

    }
}