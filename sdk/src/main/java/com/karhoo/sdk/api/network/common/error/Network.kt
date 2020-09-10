package com.karhoo.sdk.api.network.common.error

import com.karhoo.sdk.api.network.client.RequestInterceptor
import com.karhoo.sdk.api.network.header.Headers
import okhttp3.OkHttpClient

object Network {

    var httpClientBuilder = OkHttpClient.Builder()
    private var httpClient: OkHttpClient? = null

    fun httpClient(headers: Headers): OkHttpClient {
        return httpClient ?: createHttpClient(headers)
    }

    private fun createHttpClient(headers: Headers): OkHttpClient {
        httpClient = httpClientBuilder
                .addInterceptor(RequestInterceptor(headers))
                .build()
        return httpClient as OkHttpClient
    }

}
