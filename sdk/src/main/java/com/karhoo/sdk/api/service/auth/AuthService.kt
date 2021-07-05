package com.karhoo.sdk.api.service.auth

import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.call.Call

interface AuthService {

    fun login(token: String): Call<UserInfo>

    fun loggedIn(credentials: Credentials): Call<UserInfo>

    fun revoke(): Call<Void>
}
