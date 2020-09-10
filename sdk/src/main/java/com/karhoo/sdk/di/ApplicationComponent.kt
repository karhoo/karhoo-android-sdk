package com.karhoo.sdk.di

import com.karhoo.sdk.api.datastore.user.KarhooUserStore
import com.karhoo.sdk.api.service.address.KarhooAddressService
import com.karhoo.sdk.api.service.auth.KarhooAuthService
import com.karhoo.sdk.api.service.availability.KarhooAvailabilityService
import com.karhoo.sdk.api.service.config.KarhooConfigService
import com.karhoo.sdk.api.service.drivertracking.KarhooDriverTrackingService
import com.karhoo.sdk.api.service.fare.KarhooFareService
import com.karhoo.sdk.api.service.payments.KarhooPaymentsService
import com.karhoo.sdk.api.service.quotes.KarhooQuotesService
import com.karhoo.sdk.api.service.trips.KarhooTripsService
import com.karhoo.sdk.api.service.user.KarhooUserService
import dagger.Component

@Component(modules = [
    NetworkModule::class,
    UserModule::class,
    InteractorModule::class,
    AnalyticsModule::class])
interface ApplicationComponent {

    fun inject(addressService: KarhooAddressService)

    fun inject(userService: KarhooUserService)

    fun inject(userStore: KarhooUserStore)

    fun inject(paymentsService: KarhooPaymentsService)

    fun inject(tripsService: KarhooTripsService)

    fun inject(driverTrackingService: KarhooDriverTrackingService)

    fun inject(quotesService: KarhooQuotesService)

    fun inject(availabilityService: KarhooAvailabilityService)

    fun inject(configService: KarhooConfigService)

    fun inject(fareService: KarhooFareService)

    fun inject(authService: KarhooAuthService)

}
