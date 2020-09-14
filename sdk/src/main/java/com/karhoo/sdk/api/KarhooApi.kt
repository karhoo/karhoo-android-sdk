package com.karhoo.sdk.api

import com.karhoo.sdk.api.datastore.user.KarhooUserStore
import com.karhoo.sdk.api.datastore.user.UserStore
import com.karhoo.sdk.api.network.header.CustomHeaders
import com.karhoo.sdk.api.service.address.AddressService
import com.karhoo.sdk.api.service.address.KarhooAddressService
import com.karhoo.sdk.api.service.auth.AuthService
import com.karhoo.sdk.api.service.auth.KarhooAuthService
import com.karhoo.sdk.api.service.config.ConfigService
import com.karhoo.sdk.api.service.config.KarhooConfigService
import com.karhoo.sdk.api.service.drivertracking.DriverTrackingService
import com.karhoo.sdk.api.service.drivertracking.KarhooDriverTrackingService
import com.karhoo.sdk.api.service.fare.FareService
import com.karhoo.sdk.api.service.fare.KarhooFareService
import com.karhoo.sdk.api.service.payments.KarhooPaymentsService
import com.karhoo.sdk.api.service.payments.PaymentsService
import com.karhoo.sdk.api.service.quotes.KarhooQuotesService
import com.karhoo.sdk.api.service.quotes.QuotesService
import com.karhoo.sdk.api.service.trips.KarhooTripsService
import com.karhoo.sdk.api.service.trips.TripsService
import com.karhoo.sdk.api.service.user.KarhooUserService
import com.karhoo.sdk.api.service.user.UserService
import com.karhoo.sdk.di.AnalyticsModule
import com.karhoo.sdk.di.DaggerApplicationComponent
import com.karhoo.sdk.di.UserModule

/**
 * Single entry point into the whole Karhoo API
 */
object KarhooApi : KarhooService {

    override val addressService: AddressService = KarhooAddressService()

    override val userService: UserService = KarhooUserService()

    override val userStore: UserStore = KarhooUserStore()

    override val paymentsService: PaymentsService = KarhooPaymentsService()

    override val tripService: TripsService = KarhooTripsService()

    override val driverTrackingService: DriverTrackingService = KarhooDriverTrackingService()

    override val quotesService: QuotesService = KarhooQuotesService()

    override val configService: ConfigService = KarhooConfigService()

    override val fareService: FareService = KarhooFareService()

    override val authService: AuthService = KarhooAuthService()

    internal var apiKey: String? = null

    internal var customHeaders: CustomHeaders = object : CustomHeaders {
        override val headers: Map<String, String> = emptyMap()
    }

    override fun setConfiguration(configuration: KarhooSDKConfiguration) {
        KarhooSDKConfigurationProvider.setConfig(configuration)

        with(DaggerApplicationComponent.builder()
                     .userModule(UserModule(configuration.context()))
                     .analyticsModule(AnalyticsModule())
                     .build()) {
            inject(addressService as KarhooAddressService)
            inject(userService as KarhooUserService)
            inject(userStore as KarhooUserStore)
            inject(paymentsService as KarhooPaymentsService)
            inject(tripService as KarhooTripsService)
            inject(driverTrackingService as KarhooDriverTrackingService)
            inject(quotesService as KarhooQuotesService)
            inject(configService as KarhooConfigService)
            inject(fareService as KarhooFareService)
            inject(authService as KarhooAuthService)
        }
    }

}
