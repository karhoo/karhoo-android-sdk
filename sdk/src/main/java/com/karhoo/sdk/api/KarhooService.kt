package com.karhoo.sdk.api

import com.karhoo.sdk.api.datastore.user.UserStore
import com.karhoo.sdk.api.service.address.AddressService
import com.karhoo.sdk.api.service.auth.AuthService
import com.karhoo.sdk.api.service.config.ConfigService
import com.karhoo.sdk.api.service.drivertracking.DriverTrackingService
import com.karhoo.sdk.api.service.fare.FareService
import com.karhoo.sdk.api.service.loyalty.LoyaltyService
import com.karhoo.sdk.api.service.payments.PaymentsService
import com.karhoo.sdk.api.service.quotes.QuotesService
import com.karhoo.sdk.api.service.trips.TripsService
import com.karhoo.sdk.api.service.user.UserService

interface KarhooService {

    fun setConfiguration(configuration: KarhooSDKConfiguration)

    val authService: AuthService

    val addressService: AddressService

    val userService: UserService

    val userStore: UserStore

    val paymentsService: PaymentsService

    val tripService: TripsService

    val driverTrackingService: DriverTrackingService

    val quotesService: QuotesService

    val configService: ConfigService

    val fareService: FareService

    val loyaltyService: LoyaltyService

}
