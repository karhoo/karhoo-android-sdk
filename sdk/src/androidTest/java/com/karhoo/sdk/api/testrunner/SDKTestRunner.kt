package com.karhoo.sdk.api.testrunner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.karhoo.sdk.api.network.common.error.Network
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class SDKTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        val applicationClassName = SDKApplication::class.java.name

        Network.httpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)

        return super.newApplication(cl, applicationClassName, context)
    }

}