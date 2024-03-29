package com.karhoo.sdk.api.util

import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.givenThat
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.BuildConfig
import com.karhoo.sdk.api.model.*
import com.karhoo.sdk.api.model.adyen.AdyenPublicKey
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.client.APITemplate.Companion.IDENTIFIER_ID
import com.karhoo.sdk.api.network.request.CoverageRequest
import com.karhoo.sdk.api.network.request.QuoteQTA
import java.util.Date

fun serverRobot(func: ServerRobot.() -> Unit) = ServerRobot().apply { func() }

class ServerRobot {

    private val gson: Gson = Gson()

    fun tokenResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.TOKEN_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun successfulToken() {
        mockPostResponse(
                code = 200,
                response = TOKEN,
                endpoint = APITemplate.TOKEN_METHOD
                        )

        mockPostResponse(
                code = 200,
                response = TOKEN,
                endpoint = APITemplate.TOKEN_REFRESH_METHOD
                        )

    }

    fun passwordResetResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.PASSWORD_RESET_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun userProfileResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.USER_PROFILE_METHOD,
                delayInMillis = delayInMillis
                       )
    }

    fun registerUserResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.REGISTER_USER_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun paymentsNonceResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.NONCE_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun sdkInitResponse(code: Int, response: Any, delayInMillis: Int = 0, appendedUrl: String) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.SDK_INITIALISER_METHOD + appendedUrl,
                delayInMillis = delayInMillis
                        )
    }

    fun addCardResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.ADD_CARD_METHOD,
                delayInMillis = delayInMillis

                        )
    }

    fun addressListResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.ADDRESS_AUTOCOMPLETE_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun addressDetails(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.PLACE_DETAILS_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun reverseGeocodeResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.REVERSE_GEO_METHOD,
                delayInMillis = delayInMillis)
    }

    fun quoteIdResponse(code: Int, response: Any, endpoint: String = APITemplate
            .QUOTES_REQUEST_METHOD, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = endpoint,
                delayInMillis = delayInMillis
                        )
    }

    fun quotesResponse(code: Int, response: Any, endpoint: String = APITemplate.QUOTES_METHOD,
                         delayInMillis: Int = 0, quoteId: String = QUOTE_ID.quoteId) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = endpoint.replace("{$IDENTIFIER_ID}", quoteId),
                delayInMillis = delayInMillis
                       )
    }

    fun quoteImagesRuleListResponse(
        code: Int,
        response: Any,
        endpoint: String
    ) {
        mockGetResponse(
            code = code,
            response = response,
            endpoint = endpoint
        )
    }

    fun verifyQuotesResponse(code: Int, response: Any, endpoint: String = APITemplate.VERIFY_QUOTES_METHOD, delayInMillis: Int = 0, quoteId: String = QUOTE_ID.quoteId) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = endpoint.replace("{$IDENTIFIER_ID}", quoteId),
                delayInMillis = delayInMillis
        )
    }

    fun bookingResponseWithNonce(code: Int, response: Any, delayInMillis: Int = 0, header: Pair<String, String> = Pair("", "")) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.BOOKING_WITH_NONCE_METHOD,
                delayInMillis = delayInMillis,
                header = header
                        )
    }

    fun bookingResponseWithoutNonce(code: Int, response: Any, delayInMillis: Int = 0, header:
    Pair<String, String> = Pair("", "")) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.BOOKING_METHOD,
                delayInMillis = delayInMillis,
                header = header
                        )
    }

    fun bookingDetailsResponse(code: Int, response: Any, delayInMillis: Int = 0, trip: String) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.BOOKING_DETAILS_METHOD.replace("{$IDENTIFIER_ID}", trip),
                delayInMillis = delayInMillis
                       )
    }

    fun cancellationFeeResponse(code: Int, response: Any, delayInMillis: Int = 0, id: String = BOOKING_ID) {
        mockGetResponse(code = code,
                       response = response,
                       endpoint = APITemplate.BOOKING_CANCEL_FEE.replace("{$IDENTIFIER_ID}", id),
                        delayInMillis = delayInMillis)
    }

    fun cancelResponse(code: Int, response: Any, delayInMillis: Int = 0, trip: String) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.CANCEL_BOOKING_METHOD.replace("{$IDENTIFIER_ID}", trip),
                delayInMillis = delayInMillis
                        )
    }

    fun getLoyaltyBalanceResponse(code: Int, response: Any, delayInMillis: Int = 0, id: String = LOYALTY_ID) {
        mockGetResponse(code = code,
                        response = response,
                        endpoint = APITemplate.LOYALTY_BALANCE.replace("{$IDENTIFIER_ID}", id),
                        delayInMillis = delayInMillis)
    }

    fun loyaltyConversionResponse(code: Int, response: Any, delayInMillis: Int = 0, id: String = LOYALTY_ID) {
        mockGetResponse(code = code,
                       response = response,
                       endpoint = APITemplate.LOYALTY_CONVERSION.replace("{$IDENTIFIER_ID}", id),
                       delayInMillis = delayInMillis)
    }

    fun cancelGuestBookingResponse(code: Int, response: Any, delayInMillis: Int = 0, trip: String) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.CANCEL_GUEST_BOOKING_METHOD.replace("{$IDENTIFIER_ID}", trip),
                delayInMillis = delayInMillis
                        )
    }

    fun driverTrackingResponse(code: Int, response: Any, delayInMillis: Int = 0, trip: String) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.TRACK_DRIVER_METHOD.replace("{$IDENTIFIER_ID}", trip),
                delayInMillis = delayInMillis
                       )
    }

    fun driverTrackingGuestBookingResponse(code: Int, response: Any, delayInMillis: Int = 0, trip:
    String) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.GUEST_BOOKING_TRACK_DRIVER_METHOD.replace("{$IDENTIFIER_ID}", trip),
                delayInMillis = delayInMillis
                       )
    }

    fun tripStatusResponse(code: Int, response: Any, delayInMillis: Int = 0, tripId: String = BOOKING_ID) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.BOOKING_STATUS_METHOD.replace("{$IDENTIFIER_ID}", tripId),
                delayInMillis = delayInMillis
                       )
    }

    fun upcomingRidesResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.BOOKING_HISTORY_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun fareResponse(code: Int, response: Any, delayInMillis: Int = 0, tripId: String) {
        mockGetResponse(
                code = code,
                response = response,
                delayInMillis = delayInMillis,
                endpoint = APITemplate.FARE_DETAILS.replace("{$IDENTIFIER_ID}", tripId)
                       )
    }

    fun getAdyenPublicKeyResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.ADYEN_PUBLIC_KEY_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun getAdyenPaymentMethodsResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.ADYEN_PAYMENT_METHODS_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun getAdyenPaymentsResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.ADYEN_PAYMENTS_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun getAdyenPaymentsDetailsResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.ADYEN_PAYMENT_DETAILS,
                delayInMillis = delayInMillis
                        )
    }

    fun authRevokeResponse(code: Int, response: Any, delayInMillis: Int = 0, token: String) {
        mockPostResponse(code = code,
                         response = response,
                         delayInMillis = delayInMillis,
                         endpoint = APITemplate.AUTH_REVOKE_METHOD)
    }

    fun authTokenResponse(code: Int, response: Any, delayInMillis: Int = 0, header: Pair<String, String> = Pair("", "")) {
        mockPostResponse(code = code,
                         response = response,
                         delayInMillis = delayInMillis,
                         endpoint = APITemplate.AUTH_TOKEN_METHOD,
                         header = header)
    }

    fun authRefreshResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(code = code,
                         response = response,
                         delayInMillis = delayInMillis,
                         endpoint = APITemplate.AUTH_REFRESH_METHOD)
    }

    fun authUserResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockGetResponse(code = code,
                        response = response,
                        delayInMillis = delayInMillis,
                        endpoint = APITemplate.AUTH_USER_INFO_METHOD)
    }

    fun getPaymentProviderMethodsResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockGetResponse(code = code,
                        response = response,
                        delayInMillis = delayInMillis,
                        endpoint = APITemplate.PAYMENT_PROVIDERS_METHOD)
    }

    fun getCheckCoverageResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockGetResponse(code = code,
                        response = response,
                        delayInMillis = delayInMillis,
                        endpoint = APITemplate.QUOTES_CHECK_COVERAGE)
    }

    private fun mockPostResponse(code: Int,
                                 response: Any,
                                 endpoint: String,
                                 delayInMillis: Int = 0,
                                 header: Pair<String, String> = Pair("", "")) {

        val stringResponse = if (response is String) {
            response
        } else {
            gson.toJson(response)
        }

        givenThat(post(urlEqualTo(endpoint))
                          .willReturn(
                                  ResponseUtils(
                                          httpCode = code,
                                          response = stringResponse,
                                          delayInMillis = delayInMillis,
                                          header = header)
                                          .createResponse()))
    }

    private fun mockGetResponse(code: Int,
                                response: Any,
                                endpoint: String,
                                delayInMillis: Int = 0,
                                header: Pair<String, String> = Pair("", "")) {

        val stringResponse = if (response is String) {
            response
        } else {
            gson.toJson(response)
        }

        givenThat(get(urlPathEqualTo(endpoint))
                          .willReturn(
                                  ResponseUtils(
                                          httpCode = code,
                                          response = stringResponse,
                                          delayInMillis = delayInMillis,
                                          header = header)
                                          .createResponse()))
    }

    companion object {

        /**
         *
         * Bookings
         *
         */
        val BOOKING_ID = "BK123"

        val LOYALTY_ID = "LP123"

        val AVAILABILITIES = Categories(listOf("Saloon", "Taxi", "MPV", "Exec", "Electric", "Moto"))

        val QUOTE_ID = QuoteId(quoteId = "129e51a-bc10-11e8-a821-0a580a0414db")
        val SOME_OTHER_QUOTE_ID_VALUE = "someOtherQuoteId"
        val SOME_OTHER_FLEET_ID = "someOtherFleetId"

        val QUOTE_PRICE = QuotePrice(currencyCode = "GBP",
                                     highPrice = 779,
                                     lowPrice = 778)

        val QUOTE_NET_PRICE = QuotePriceNet(highPrice = 779, lowPrice = 778)

        val QUOTE_FLEET = Fleet(id = "someFleetId",
                                    name = "someFleetName",
                                    logoUrl = "someLogoUrl",
                                    description = "Some fleet description",
                                    phoneNumber = "+123",
                                    termsConditionsUrl = "someTermsUrl",
                                    capabilities = listOf("driver_details", "vehicle_details"))

        val QUOTE_FLEET_RATING = FleetRating(count = 1, score = 4)

        val QUOTE_VEHICLE = QuoteVehicle(vehicleType = "Electric",
                                        vehicleClass = "Saloon",
//                                        vehicleTags = listOf("Electric", "Taxi"),
                                        vehicleQta = QuoteQTA(highMinutes = 10, lowMinutes = 1),
                                        passengerCapacity = 4,
                                        luggageCapacity = 2)

        val QUOTE = Quote(id = "someQuoteId",
                               quoteType = QuoteType.ESTIMATED,
                               quoteSource = QuoteSource.FLEET,
                               price = QUOTE_PRICE,
                               fleet = QUOTE_FLEET,
                               pickupType = PickupType.CURBSIDE,
                               vehicle = QUOTE_VEHICLE)

        val AVAILABILITY = Availability(vehicles = AvailabilityVehicle(
                classes = listOf("Saloon", "Taxi", "MPV", "Exec", "Electric", "Moto"),
                tags = listOf(""),
                types = listOf("Electric", "Standard", "MPV")))

        val VEHICLES = Vehicles(
                status = QuoteStatus.PROGRESSING,
                id = QUOTE_ID.quoteId,
                availability = AVAILABILITY,
                quotes = listOf(
                        QUOTE,
                        QUOTE.copy(
                                id = "someOtherQuoteId",
                                quoteSource = QuoteSource.FLEET,
                                quoteType = QuoteType.METERED,
                                fleet = QUOTE_FLEET.copy(id = "someOtherFleetId"))
                               ))

        val QUOTE_IMAGES_RULE_LIST = "{\"rules\":[{\"fleet_country_code\":\"*\",\"vehicle_type\":\"standard\",\"vehicle_tags\":[],\"vehicle_image\":\"https://cdn.karhoo.com/vehicle-image/sedan-regular.jpg\"},{\"fleet_country_code\":\"*\",\"vehicle_type\":\"standard\",\"vehicle_tags\":[\"executive\"],\"vehicle_image\":\"https://cdn.karhoo.com/vehicle-image/sedan-executive.jpg\"}]}"

        val QUOTE_LIST_EMPTY = QuoteList(
                id = QuoteId(QUOTE_ID.quoteId),
                status = QuoteStatus.PROGRESSING,
                validity = 10,
                categories = mapOf(
                        "Saloon" to emptyList(),
                        "Taxi" to emptyList(),
                        "MPV" to emptyList(),
                        "Exec" to emptyList(),
                        "Electric" to emptyList(),
                        "Moto" to emptyList()))

        val BOOKINGFEE = BookingFee(true,
                                    BookingFeePrice("GBP", "", 500))

        /**
         *
         * Payments
         *
         */
//        val PAYMENTS_TOKEN = PaymentsNonce(
//                nonce = "njfdeilnvbflinvbiurnceernnvbrgtuverosa",
//                lastFour = "1234",
//                cardType = CardType.VISA)

        val PAYMENT_TOKEN = BraintreeSDKToken(
                token = "njfdeilnvbflinvbiurnceernnvbrgtuverosa")

        val LOYALTY_PROGRAMMES = LoyaltyProgramme(
                id = "JA02121981",
                name = "someLoyaltyName"
                                                 )

        val PAYMENT_PROVIDER = PaymentProvider(
                Provider(id = "Provider1234"), loyalty = LOYALTY_PROGRAMMES)

        /**
         *
         * Tokens
         *
         */
        val TOKEN = Credentials(
                accessToken = "eyJz93ak4laUWw",
                expiresIn = 86400,
                refreshToken = "sajkqoweioiuoiuoqwe")

        /**
         *
         * User
         *
         */
        val USER_INFO = UserInfo(
                userId = "1234",
                email = "name@email.com",
                firstName = "John",
                lastName = "Smith",
                phoneNumber = "1234567890",
                locale = "en-GB",
                primaryOrganisationId = "Karhoo",
                organisations = listOf(Organisation(
                        id = BuildConfig.KARHOO_STAGING_BRAINTREE_DEFAULT_ORGANISATION_ID,
                        name = "Karhoo",
                        roles = listOf("TRIP_ADMIN")
                                                   ))
                                )

        /**
         * Address Payloads
         */
        val PLACE_SEARCH_LIST = Places(
                locations = listOf(
                        Place(
                                placeId = "ChIJ3QDsadsaPI0DjT6SU",
                                displayAddress = "Terminal 1, Longford, Hounslow, UK"),
                        Place(
                                placeId = "ChIJ3QDsadsaPI0DjT6SU",
                                displayAddress = "Terminal 2, Longford, Hounslow, UK"),
                        Place(
                                placeId = "ChIJ3QDsadsaPI0DjT6SU",
                                displayAddress = "Terminal 3, Longford, Hounslow, UK"),
                        Place(
                                placeId = "ChIJ3QDsadsaPI0DjT6SU",
                                displayAddress = "Terminal 4, Longford, Hounslow, UK"),
                        Place(
                                placeId = "ChIJ3QDsadsaPI0DjT6SU",
                                displayAddress = "Terminal 5, Longford, Hounslow, UK")
                                  )
                                      )

        const val LATITUDE = 51.5166744
        const val LONGITUDE = -0.1769328

        val ADDRESS = Address(displayAddress = "Paddington Station",
                              lineOne = "",
                              lineTwo = "",
                              buildingNumber = "",
                              streetName = "Praed St",
                              city = "London",
                              postalCode = "W2 1HQ",
                              region = "Greater London",
                              countryCode = "UK")

        val LOCATION_INFO = LocationInfo(position = Position(LATITUDE, LONGITUDE),
                                         placeId = "123",
                                         poiType = Poi.REGULATED,
                                         address = ADDRESS,
                                         timezone = "UK",
                                         details = PoiDetails(iata = "iata",
                                                              terminal = "terminal",
                                                              type = null),
                                         meetingPoint = MeetingPoint(position = Position(
                                                 latitude = 51.5062894,
                                                 longitude = -0.0859324),
                                                                     instructions = "I am near by",
                                                                     pickupType = PickupType.CURBSIDE))

        const val LAT = "51.532156"
        const val LONG = "0.123838"

        /**
         *
         * Driver Tracking
         *
         */
        val DRIVER_TRACKING = DriverTrackingInfo(
                position = Position(
                        latitude = 51.5166744,
                        longitude = -0.1769328
                                   ),
                destinationEta = 10,
                originEta = 5,
                direction = Direction(kph = 5,
                                      heading = 10))

        /**
         *
         * Trip
         *
         */
        val TRIP_DER = TripInfo(
                tripId = "b6a5f9dc-9066-4252-9013-be85dfa563bc",
                tripState = TripStatus.DRIVER_EN_ROUTE,
                fleetInfo = FleetInfo(
                        name = "Antelope [Zoo]",
                        description = "Zoo Test Fleet 5",
                        fleetId = "d4e6e7df-76ac-46dd-89c9-5968949ed10a",
                        logoUrl = "https://cdn.karhoo.com/d/images/logos/cc775eda-950d-4a77-aa83-172d487a4cbf.png",
                        phoneNumber = "+447760222331",
                        termsConditionsUrl = "https://karhoo.com/fleettcs/d4e6e7df-76ac-46dd-89c9-5968949ed10a"
                                     ),
                meetingPoint = MeetingPoint(pickupType = PickupType.NOT_SET),
                quote = Price(
                        total = 500,
                        currency = "GBP",
                        quoteType = QuoteType.METERED
                             ),
                vehicle = Vehicle(
                        vehicleClass = "saloon",
                        description = "Black Prius Toyota",
                        driver = Driver(
                                firstName = "John",
                                lastName = "Nowhas Picture",
                                phoneNumber = "447234765098",
                                licenceNumber = "55555",
                                photoUrl = "https://cdn.karhoo.net/d/images/driver-photos/b0f859f345ce3eeac98d227439d26a91.jpg"
                                       ),
                        vehicleLicencePlate = "ZATLOW"
                                 ),
                origin = TripLocationInfo(
                        displayAddress = "221B Baker St, Marylebone, London NW1 6XE, UK",
                        position = Position(
                                latitude = 51.523766,
                                longitude = -0.1585557
                                           ),
                        placeId = "ChIJEYJiM88adkgR4SKDqHd2XUQ",
                        poiType = Poi.NOT_SET,
                        timezone = "Europe/London"
                                         ),
                destination = TripLocationInfo(
                        displayAddress = "221B Baker St, Marylebone, London NW1 6XE, UK",
                        placeId = "43fChIJEYJiM88adkgsdsaR4SKDqHd2XUQ",
                        poiType = Poi.NOT_SET,
                        timezone = "Europe/London",
                        position = Position(
                                latitude = 51.5237466,
                                longitude = -0.1578555
                                           )
                                              ),
                dateScheduled = Date(),
                displayTripId = "23SHUD")

        val TRIP_REQUESTED_DETAILS = TRIP_DER.copy(tripState = TripStatus.REQUESTED)

        val TRIP_LIST_COMPLETED = TripList(listOf(TRIP_DER.copy(
                tripState = TripStatus.COMPLETED)
                                                 ))

        /**
         *
         * Loyalty
         *
         */

        val LOYALTY_BALANCE = LoyaltyBalance(points = 123,
                                            burnable = false)

        val LOYALTY_CONVERSION = LoyaltyConversion(version = "20200312",
                                                  rates = listOf(LoyaltyRates(currency = "GBP",
                                                                             points = 5.0)))

        /**
         *
         * Trip Status
         *
         */

        val TRIP_STATE = TripState(TripStatus.NO_DRIVERS)

        /**
         *
         * Fares
         *
         */
        val FARE_BREAKDOWN = FareBreakdown(
                currency = "EUR",
                total = 15)

        val FARE = Fare(
                state = "PENDING",
                breakdown = FARE_BREAKDOWN)

        val ADYEN_PUBLIC_KEY = AdyenPublicKey(publicKey = "12234455")

        /**
         *
         * Coverage
         *
         */

        val COVERAGE_OK = Coverage(true)

        val COVERAGE_REQUEST = CoverageRequest(LAT, LONG, "")

        /**
         *
         * Errors
         *
         */
        val GENERAL_ERROR = KarhooInternalError(code = "K0001")

        val K3001_ERROR = KarhooInternalError(code = "K3001")

        data class KarhooInternalError(@SerializedName("code") val code: String)

        /**
         *
         * Data Issue Payloads
         *
         */
        const val INVALID_DATA = "{\"invalid\": \"data\"}"

        const val INVALID_JSON = "{\n\"invalid\": json\n\"hello\": invalid\n}"

        const val NO_BODY = ""

        const val EMPTY = "{}"
    }

}

