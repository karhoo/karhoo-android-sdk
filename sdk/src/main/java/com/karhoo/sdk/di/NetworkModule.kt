package com.karhoo.sdk.di

import com.google.gson.GsonBuilder
import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.EnvironmentDetails
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.adapter.SealedCoroutineCallAdapterFactory
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.client.DateTypeAdapter
import com.karhoo.sdk.api.network.common.error.Network.httpClient
import com.karhoo.sdk.api.network.header.Headers
import com.karhoo.sdk.api.network.header.KarhooHeaders
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Date

@Module
class NetworkModule {

    @Provides
    internal fun provideAPITemplate(headers: Headers, analytics: Analytics): APITemplate {
        val client = httpClient(headers)

        val gson = GsonBuilder().registerTypeAdapter(Date::class.java, DateTypeAdapter()).create()
        val environment = KarhooSDKConfigurationProvider.configuration.environment()
        val environmentDetails = EnvironmentDetails(environment = environment)

        val retrofit = Retrofit.Builder()
                .baseUrl(environmentDetails.host)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(SealedCoroutineCallAdapterFactory(analytics))
                .build()

        return retrofit.create(APITemplate::class.java)
    }

    @Provides
    internal fun provideHeaders(credentialsManager: CredentialsManager): Headers = KarhooHeaders(
            credentialsManager = credentialsManager)

}
