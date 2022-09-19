package com.karhoo.sdk.api

import android.content.Context
import com.karhoo.sdk.analytics.AnalyticProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials

interface KarhooSDKConfiguration {

    fun environment(): KarhooEnvironment

    fun context(): Context

    fun authenticationMethod(): AuthenticationMethod

    fun analyticsProvider(): AnalyticProvider?

    suspend fun requestExternalAuthentication(callback: () -> Unit)
}
