package com.karhoo.sdk.api.testrunner

import android.content.Context
import com.karhoo.sdk.analytics.AnalyticProvider
import com.karhoo.sdk.api.KarhooEnvironment
import com.karhoo.sdk.api.KarhooSDKConfiguration
import com.karhoo.sdk.api.model.AuthenticationMethod

class UnitTestSDKConfig(val context: Context, val authenticationMethod: AuthenticationMethod =
        AuthenticationMethod.KarhooUser()) :
        KarhooSDKConfiguration {

    override fun environment(): KarhooEnvironment {
        return KarhooEnvironment.Sandbox()
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
}
