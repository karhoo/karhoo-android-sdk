package com.karhoo.sdk.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.testrunner.SDKTestConfig
import com.karhoo.sdk.api.util.ServerRobot.Companion.EMPTY
import com.karhoo.sdk.api.util.ServerRobot.Companion.GENERAL_ERROR
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_DATA
import com.karhoo.sdk.api.util.ServerRobot.Companion.INVALID_JSON
import com.karhoo.sdk.api.util.ServerRobot.Companion.NO_BODY
import com.karhoo.sdk.api.util.ServerRobot.Companion.USER_INFO
import com.karhoo.sdk.api.util.TestData
import com.karhoo.sdk.api.util.serverRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RegisterIntegrationTest {

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
        wireMockRule.resetAll()
    }

    /**
     * Given:   Register is requested
     * When:    Successful response has been returned
     * Then:    The response payload should be valid
     **/
    @Test
    fun registerSuccess() {
        serverRobot {
            registerUserResponse(HTTP_CREATED, USER_INFO)
        }

        var result: UserInfo? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(USER_INFO)
    }

    /**
     * Given:   Register is requested
     * When:    Success 201 but with invalid data
     * Then:    An internal sdk error should be returned
     **/
    @Test
    fun invalidDataWhenRequestingRegisteredUserInfoReturnsBlankUserInfo() {
        serverRobot {
            registerUserResponse(code = HTTP_CREATED, response = INVALID_DATA)
        }

        var result: UserInfo? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Success -> {
                    result = it.data
                    latch.countDown()
                }
                is Resource.Failure -> fail(it.error.internalMessage)
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result?.firstName).isBlank()
    }

    /**
     * Given:   Register is requested
     * When:    Success 201 but with bad json
     * Then:    An error should be returned
     **/
    @Test
    fun badJsonSuccessReturnsError() {
        serverRobot {
            registerUserResponse(code = HTTP_CREATED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Register is requested
     * When:    Success 204 but with no body
     * Then:    An error should be returned
     **/
    @Test
    fun noBodyReturnsError() {
        serverRobot {
            registerUserResponse(code = HTTP_CREATED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Register is requested
     * When:    Error 401 with error payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseGetsParsedIntoKarhooError() {
        serverRobot {
            registerUserResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.GeneralRequestError)
    }

    /**
     * Given:   Register is requested
     * When:    Error 401 with no body payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithNoBodyGetsParsedIntoKarhooError() {
        serverRobot {
            registerUserResponse(code = HTTP_UNAUTHORIZED, response = NO_BODY)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Register is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithEmptyBodyGetsParsedIntoKarhooError() {
        serverRobot {
            registerUserResponse(code = HTTP_UNAUTHORIZED, response = EMPTY)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Register is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidJsonGetsParsedIntoKarhooError() {
        serverRobot {
            registerUserResponse(code = HTTP_UNAUTHORIZED, response = INVALID_JSON)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Register is requested
     * When:    Error 401 with empty payload
     * Then:    The karhoo error should be valid
     **/
    @Test
    fun errorResponseWithInvalidDataGetsParsedIntoKarhooError() {
        serverRobot {
            registerUserResponse(code = HTTP_UNAUTHORIZED, response = INVALID_DATA)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Unexpected)
    }

    /**
     * Given:   Register is requested
     * When:    The register response takes too long
     * Then:    The timeout error should be returned
     **/
    @Test
    fun timeoutErrorReturnedWhenResponseTakesTooLong() {
        serverRobot {
            registerUserResponse(code = HTTP_UNAUTHORIZED, response = GENERAL_ERROR, delayInMillis = 2000)
        }

        var result: KarhooError? = null

        KarhooApi.userService.register(TestData.USER_REGISTRATION).execute {
            when (it) {
                is Resource.Failure -> {
                    result = it.error
                    latch.countDown()
                }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(KarhooError.Timeout)
    }

}