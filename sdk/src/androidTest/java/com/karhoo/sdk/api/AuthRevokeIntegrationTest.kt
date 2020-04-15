package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.testrunner.TestSDKConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AuthRevokeIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = TestSDKConfig(context =
                                                                                   InstrumentationRegistry.getInstrumentation().context,
                                                                                   authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        serverRobot {
            successfulToken()
        }
    }

    @After
    fun tearDown() {
        wireMockRule.resetAll()
    }

    /**
     * Given:   revoke token
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun revokeSuccess() {
        serverRobot {
            authRevokeResponse(code = HTTP_NO_CONTENT, response = EMPTY, token = "123")
        }

        var result: Void? = null

        KarhooApi.authService.revoke().execute {
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
}
