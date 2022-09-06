package com.karhoo.sdk.api.testrunner

import android.content.Context
import com.karhoo.sdk.analytics.AnalyticProvider
import com.karhoo.sdk.api.KarhooEnvironment
import com.karhoo.sdk.api.KarhooSDKConfiguration
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials

class TestSDKConfig(val context: Context, private val authenticationMethod: AuthenticationMethod =
        AuthenticationMethod.KarhooUser()) :
        KarhooSDKConfiguration {

    override fun environment(): KarhooEnvironment {
        return KarhooEnvironment.Custom(host = "http://127.0.0.1:8089", authHost = "", guestHost = "")
    }

    override fun context(): Context {
        return context
    }

    override fun authenticationMethod(): AuthenticationMethod {
        return authenticationMethod
    }

    override fun analyticsProvider(): AnalyticProvider? {
        return null
    }

    override suspend fun requestNewAuthenticationCredentials(callback: suspend (Credentials) -> Unit) {
        //Do nothing
    }
}