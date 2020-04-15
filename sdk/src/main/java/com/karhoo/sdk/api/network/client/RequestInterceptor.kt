package com.karhoo.sdk.api.network.client

import com.karhoo.sdk.api.EnvironmentDetails
import com.karhoo.sdk.api.KarhooApi
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.network.header.Headers
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RequestInterceptor(private val headers: Headers) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val updatedRequestBuilder = request.newBuilder()
                .addHeader("correlation_id", headers.generateCorrelationId(request.url().encodedPath()))
                .addHeader("Content-Type", headers.contentType)

        when (val config = KarhooSDKConfigurationProvider.configuration.authenticationMethod()) {
            is AuthenticationMethod.TokenExchange -> updatedRequestBuilder.addHeader("authorization", "Bearer " + headers.authenticationToken)
            is AuthenticationMethod.KarhooUser -> updatedRequestBuilder.addHeader("authorization", "Bearer " + headers.authenticationToken)
            is AuthenticationMethod.Guest -> {
                updatedRequestBuilder.addHeader("identifier", config.identifier)
                updatedRequestBuilder.addHeader("referer", config.referer)
                updateBaseUrl(request, updatedRequestBuilder)
            }
        }

        KarhooApi.apiKey?.let {
            updatedRequestBuilder.addHeader("Api-Key", it)
        }

        KarhooApi.customHeaders.headers.forEach {
            updatedRequestBuilder.addHeader(it.key, it.value)
        }

        return chain.proceed(updatedRequestBuilder.build())
    }

    private fun updateBaseUrl(request: Request, updatedRequestBuilder: Request.Builder) {
        val old = request.url().host()
        HttpUrl.parse(EnvironmentDetails.current().guestHost)?.url()?.let {
            updatedRequestBuilder
                    .url(HttpUrl.parse(request.url().toString().replace(old, it.host))
                                 ?: request.url())
                    .build()
        }
    }

}
