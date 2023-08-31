package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.testrunner.TestSDKConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.TOKEN
import com.karhoo.sdk.api.util.ServerRobot.Companion.USER_INFO
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_OK
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AuthLoginIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(SDKTestConfig.wireMockOptions)

    private val latch = CountDownLatch(1)

    @Before
    fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = TestSDKConfig(context =
                                                                               InstrumentationRegistry.getInstrumentation().context,
                                                                               authenticationMethod = AuthenticationMethod.TokenExchange("clientId", "scope")))
        serverRobot {
            authTokenResponse(code = HTTP_OK, response = TOKEN,
                              header = Pair("accept", "application/json"))
            authUserResponse(code = HTTP_OK, response = USER_INFO)
        }
    }

    @After
    fun tearDown() {
        KarhooSDKConfigurationProvider.setConfig(configuration = TestSDKConfig(context =
                                                                               InstrumentationRegistry.getInstrumentation().context,
                                                                               authenticationMethod = AuthenticationMethod.KarhooUser()))
        wireMockRule.resetAll()
    }

    /**
     * Given:   The user logs in
     * When:    A successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun loginWithTokenSuccess() {
        var result: UserInfo? = null

        KarhooApi.authService.login("123").execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }

    /**
     * Given:   The user logs in
     * When:    A successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun loginSuccess() {
        var result: UserInfo? = null

        KarhooApi.authService.login(Credentials(accessToken = "123456", refreshToken = "zxy", expiresIn = 1L)).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }

                else -> {}
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isNotNull
    }
}
