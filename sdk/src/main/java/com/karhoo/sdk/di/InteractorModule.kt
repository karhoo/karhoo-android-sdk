package com.karhoo.sdk.di

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.service.availability.AvailabilityInteractor
import com.karhoo.sdk.api.service.availability.AvailabilityService
import com.karhoo.sdk.api.service.availability.KarhooAvailabilityService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
class InteractorModule {

    @ExperimentalCoroutinesApi
    @Provides
    internal fun provideAvailabilityInteractor(credentialsManager: CredentialsManager,
                                               apiTemplate: APITemplate): AvailabilityInteractor = AvailabilityInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate,
            context = Unconfined)

    @Provides
    internal fun provideAvailabilityService(availabilityInteractor: AvailabilityInteractor)
            : AvailabilityService = KarhooAvailabilityService(availabilityInteractor)

}