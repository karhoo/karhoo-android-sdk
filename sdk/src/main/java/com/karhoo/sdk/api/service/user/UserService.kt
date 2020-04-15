package com.karhoo.sdk.api.service.user

import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.network.request.UserDetailsUpdateRequest
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.request.UserRegistration
import com.karhoo.sdk.call.Call

interface UserService {

    fun register(userRegistration: UserRegistration): Call<UserInfo>

    fun loginUser(userLogin: UserLogin): Call<UserInfo>

    fun resetPassword(email: String): Call<Void>

    fun updateUserDetails(userDetailsUpdateRequest: UserDetailsUpdateRequest): Call<UserInfo>

    fun logout()

}
