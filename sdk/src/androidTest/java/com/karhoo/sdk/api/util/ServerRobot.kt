package com.karhoo.sdk.api.util

import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.givenThat
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.karhoo.sdk.api.model.Address
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.CardType
import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.model.Driver
import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.api.model.FareBreakdown
import com.karhoo.sdk.api.model.FleetInfo
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.MeetingPoint
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.PaymentsNonce
import com.karhoo.sdk.api.model.PickupType
import com.karhoo.sdk.api.model.Place
import com.karhoo.sdk.api.model.Places
import com.karhoo.sdk.api.model.Poi
import com.karhoo.sdk.api.model.PoiDetails
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.model.Price
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuoteSource
import com.karhoo.sdk.api.model.QuoteType
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.model.TripList
import com.karhoo.sdk.api.model.TripLocationInfo
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.model.TripStatus
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.model.Vehicle
import com.karhoo.sdk.api.model.Vehicles
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.client.APITemplate.Companion.identifierId
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

    fun availabilitiesResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.AVAILABILITY_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun quoteIdResponse(code: Int, response: Any, delayInMillis: Int = 0) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.QUOTE_REQUEST_METHOD,
                delayInMillis = delayInMillis
                        )
    }

    fun quotesResponse(code: Int, response: Any, delayInMillis: Int = 0, quoteId: String = QUOTE_ID.quoteId) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.QUOTES_METHOD.replace("{$identifierId}", quoteId),
                delayInMillis = delayInMillis
                       )
    }

    fun bookingResponse(code: Int, response: Any, delayInMillis: Int = 0, header: Pair<String, String> = Pair("", "")) {
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
                endpoint = APITemplate.BOOKING_DETAILS_METHOD.replace("{$identifierId}", trip),
                delayInMillis = delayInMillis
                       )
    }

    fun cancelResponse(code: Int, response: Any, delayInMillis: Int = 0, trip: String) {
        mockPostResponse(
                code = code,
                response = response,
                endpoint = APITemplate.CANCEL_BOOKING_METHOD.replace("{$identifierId}", trip),
                delayInMillis = delayInMillis
                        )
    }

    fun driverTrackingResponse(code: Int, response: Any, delayInMillis: Int = 0, trip: String) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.TRACK_DRIVER_METHOD.replace("{$identifierId}", trip),
                delayInMillis = delayInMillis
                       )
    }

    fun tripStatusResponse(code: Int, response: Any, delayInMillis: Int = 0, tripId: String = BOOKING_ID) {
        mockGetResponse(
                code = code,
                response = response,
                endpoint = APITemplate.BOOKING_STATUS_METHOD.replace("{$identifierId}", tripId),
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
                endpoint = APITemplate.FARE_DETAILS.replace("{$identifierId}", tripId)
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

        val AVAILABILITIES = Categories(listOf("Saloon", "Taxi", "MPV", "Exec", "Electric", "Moto"))

        val QUOTE_ID = QuoteId(quoteId = "129e51a-bc10-11e8-a821-0a580a0414db")

        val QUOTE = Quote(availabilityId = "NTIxMjNiZDktY2M5OC00YjhkLWE5OGEtMTIyNDQ2ZDY5ZTc5O3NhbG9vbg==",
                          categoryName = "Exec",
                          currencyCode = "GBP",
                          fleetId = "someFleetId",
                          supplierName = "someFleetName",
                          highPrice = 779,
                          lowPrice = 778,
                          phoneNumber = "+123",
                          qta = 2,
                          quoteId = "someQuoteId",
                          quoteType = QuoteType.ESTIMATED,
                          logoUrl = "someLogoUrl",
                          termsAndConditions = "someTermsUrl",
                          quoteSource = QuoteSource.FLEET,
                          vehicleClass = "saloon")

        val VEHICLES = Vehicles(
                vehicles = listOf(
                        QUOTE,
                        QUOTE.copy(
                                fleetId = "4f596e3f-c638-4221-9e88-b24bc7b4dea5",
                                quoteSource = QuoteSource.FLEET,
                                termsAndConditions = "https://karhoo.com/fleettcs/cdda3d54-2926-451f-b839-4201c9adc9f5",
                                phoneNumber = "+447715364890",
                                logoUrl = "https://cdn.karhoo.com/d/images/logos/cc775eda-950d-4a77-aa83-172d487a4cbf.png",
                                quoteType = QuoteType.METERED,
                                availabilityId = "NGY1OTZlM2YtYzYzOC00MjIxLTllODgtYjI0YmM3YjRkZWE1O3RheGk=",
                                categoryName = "Taxi",
                                lowPrice = 841,
                                highPrice = 841,
                                supplierName = "QA_base_ex_com_ex_tax_metered",
                                vehicleClass = "taxi",
                                qta = 2,
                                quoteId = "eb00db4d-44bb-11e9-bdab-0a580a04005f:NGY1OTZlM2YtYzYzOC00MjIxLTllODgtYjI0YmM3YjRkZWE1O3RheGk=")

                                 ),
                status = "PROGRESSING",
                id = QUOTE_ID.quoteId,
                categoryNames = AVAILABILITIES.categoryNames.orEmpty())

        val QUOTES = QuoteList(
                categories = mapOf(
                        "Saloon" to emptyList(),
                        "Taxi" to listOf(QUOTE.copy(
                                fleetId = "4f596e3f-c638-4221-9e88-b24bc7b4dea5",
                                quoteSource = QuoteSource.FLEET,
                                termsAndConditions = "https://karhoo.com/fleettcs/cdda3d54-2926-451f-b839-4201c9adc9f5",
                                phoneNumber = "+447715364890",
                                logoUrl = "https://cdn.karhoo.com/d/images/logos/cc775eda-950d-4a77-aa83-172d487a4cbf.png",
                                quoteType = QuoteType.METERED,
                                availabilityId = "NGY1OTZlM2YtYzYzOC00MjIxLTllODgtYjI0YmM3YjRkZWE1O3RheGk=",
                                categoryName = "Taxi",
                                lowPrice = 841,
                                highPrice = 841,
                                supplierName = "QA_base_ex_com_ex_tax_metered",
                                vehicleClass = "taxi",
                                qta = 2,
                                quoteId = "eb00db4d-44bb-11e9-bdab-0a580a04005f:NGY1OTZlM2YtYzYzOC00MjIxLTllODgtYjI0YmM3YjRkZWE1O3RheGk=")),
                        "MPV" to emptyList(),
                        "Exec" to listOf(QUOTE),
                        "Electric" to emptyList(),
                        "Moto" to emptyList()),
                id = QUOTE_ID
                              )

        val QUOTE_LIST_EMPTY = QuoteList(
                id = QuoteId(QUOTE_ID.quoteId),
                categories = mapOf(
                        "Saloon" to emptyList(),
                        "Taxi" to emptyList(),
                        "MPV" to emptyList(),
                        "Exec" to emptyList(),
                        "Electric" to emptyList(),
                        "Moto" to emptyList()))
        /**
         *
         * Payments
         *
         */
        val PAYMENTS_TOKEN = PaymentsNonce(
                nonce = "njfdeilnvbflinvbiurnceernnvbrgtuverosa",
                lastFour = "1234",
                cardType = CardType.VISA)

        val PAYMENT_TOKEN = BraintreeSDKToken(
                token = "njfdeilnvbflinvbiurnceernnvbrgtuverosa")

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
                        id = "5a54722d-e699-4da6-801f-a5652e6e31f7",
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
                originEta = 5
                                                )

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
                        email = "lova@karhoo.com",
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
