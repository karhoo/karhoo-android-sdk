package com.karhoo.sdk.api.testrunner

import android.app.Application
import com.karhoo.sdk.api.KarhooApi

class SDKApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KarhooApi.setConfiguration(configuration = TestSDKConfig(context = this
                .applicationContext))
    }

}
