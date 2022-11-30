package com.karhoo.sdk.api

import android.content.Context
import com.karhoo.sdk.analytics.AnalyticProvider
import com.karhoo.sdk.api.model.AuthenticationMethod

interface KarhooSDKConfiguration {

    fun environment(): KarhooEnvironment

    fun context(): Context

    fun authenticationMethod(): AuthenticationMethod

    fun analyticsProvider(): AnalyticProvider?

    suspend fun requireSDKAuthentication(callback: () -> Unit)
}
