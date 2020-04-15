package com.karhoo.sdk.api.util

import com.karhoo.sdk.api.KarhooApi
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.util.ServerRobot.Companion.PAYMENTS_TOKEN
import com.karhoo.sdk.api.util.TestData.Companion.USER_INFO
import java.net.HttpURLConnection

fun preferences(func: PreferencesRobot.() -> Unit) = PreferencesRobot().apply { func() }

class PreferencesRobot {

    fun setUserPreference(userInfo: UserInfo) {
        serverRobot {
            successfulToken()
            userProfileResponse(HttpURLConnection.HTTP_OK, USER_INFO)
            paymentsNonceResponse(HttpURLConnection.HTTP_OK, PAYMENTS_TOKEN)
        }
        loginUser(userInfo)
    }

    private fun loginUser(userInfo: UserInfo) {
        KarhooApi.userService.logout()
        KarhooApi.userService.loginUser(UserLogin(
                email = userInfo.email,
                password = "testpassword")).execute {
            when (it) {
                is Resource.Success -> print("User updated successfully " + it.data)
                is Resource.Failure -> print("User update failed")
            }
        }
    }

    fun clearUserPreference() {
        KarhooApi.userService.logout()
    }
}