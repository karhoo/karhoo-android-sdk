package com.karhoo.sdk.api.util

import com.karhoo.sdk.BuildConfig
import com.karhoo.sdk.api.model.Address
import com.karhoo.sdk.api.model.BraintreeSDKToken
import com.karhoo.sdk.api.model.CancellationReason
import com.karhoo.sdk.api.model.Direction
import com.karhoo.sdk.api.model.Driver
import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.api.model.FareBreakdown
import com.karhoo.sdk.api.model.FleetInfo
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.MeetingPoint
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.PickupType
import com.karhoo.sdk.api.model.Poi
import com.karhoo.sdk.api.model.PoiDetails
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.model.Price
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuoteSource
import com.karhoo.sdk.api.model.QuoteStatus
import com.karhoo.sdk.api.model.QuoteType
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.model.TripLocationInfo
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.model.TripStatus
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.model.Vehicle
import com.karhoo.sdk.api.network.request.Luggage
import com.karhoo.sdk.api.network.request.Passengers
import com.karhoo.sdk.api.network.request.PlaceSearch
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.request.TripCancellation
import com.karhoo.sdk.api.network.request.TripSearch
import com.karhoo.sdk.api.network.request.UserLogin
import com.karhoo.sdk.api.network.request.UserRegistration
import com.karhoo.sdk.api.service.common.InteractorContants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class TestData {

    companion object {

        //Requests
        val USER_LOGIN = UserLogin(
                email = "name@email.com",
                password = "1234567890")

        val PLACE_SEARCH = PlaceSearch(
                position = Position(
                        latitude = 0.0,
                        longitude = 0.0),
                query = "",
                sessionToken = "")

        val USER_REGISTRATION = UserRegistration(
                firstName = "John",
                lastName = "Smith",
                email = "name@email.com",
                password = "password",
                phoneNumber = "1234567890",
                locale = "en-GB")

        const val BOOKING_ID = "BK123"

        val BOOK_TRIP_WITH_NONCE = TripBooking(
                nonce = "123ABC",
                quoteId = "1234567890",
                passengers = Passengers(
                        additionalPassengers = 1,
                        passengerDetails = listOf(),
                        luggage = Luggage(total = 2)))

        val BOOK_TRIP_INVOICE = TripBooking(
                quoteId = "1234567890",
                passengers = Passengers(
                        additionalPassengers = 1,
                        passengerDetails = listOf(),
                        luggage = Luggage(total = 2)))

        val TRIP_SEARCH = TripSearch()

        val CANCEL = TripCancellation(tripIdentifier = "1234", reason = CancellationReason
                .OTHER_USER_REASON)

        val QUOTE_SEARCH = QuotesSearch(origin = LocationInfo(placeId = "123",
                position = Position(1.0, -1.0),
                address = Address()),
                destination = LocationInfo(placeId = "321",
                        position = Position(-1.0, 1.0),
                        address = Address()),
                dateScheduled = null)

        //Responses
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

        val USER_INFO = UserInfo(
                firstName = "John",
                lastName = "Smith",
                email = "name@email.com",
                userId = "1234",
                phoneNumber = "1234567890",
                locale = "en-GB",
                organisations = listOf(Organisation(
                        id = BuildConfig.KARHOO_STAGING_BRAINTREE_DEFAULT_ORGANISATION_ID,
                        name = "Karhoo",
                        roles = listOf("TRIP_ADMIN", InteractorContants.MOBILE_USER)))
        )

        val USER_INFO_INVALID_ORG = USER_INFO.copy(organisations = listOf(Organisation(
                id = "5a54722d-e699-4da6-801f-a5652e6e31f7",
                name = "Karhoo",
                roles = listOf("INVALID_ROLE")))
        )

        val DRIVER_TRACKING_INFO = DriverTrackingInfo(position = Position(LATITUDE, LONGITUDE),
                                                      originEta = 5,
                                                      destinationEta = 10,
                                                      direction = Direction(kph = 5,
                                                                            heading = 10))

        val TRIP_INFO_BLANK = TripInfo()

        val POSITION = Position(
                latitude = LATITUDE,
                longitude = LONGITUDE)

        val TRIP_POSITION = Position(
                latitude = 51.5086692,
                longitude = -0.1375291)

        val TRIP_LOCATION_INFO = TripLocationInfo(
                displayAddress = "2 Eastborne Terrace",
                position = TRIP_POSITION,
                placeId = "EhpCcm93bmVsbCBTdCwgU2hlZmZpZWxkLCBVSw",
                timezone = "Europe/London",
                poiType = Poi.NOT_SET)

        val PRICE = Price(
                total = 3550,
                currency = "GBP")

        val FLEET_INFO = FleetInfo(
                fleetId = "some fleet id",
                name = "some fleet name",
                phoneNumber = "some phone number",
                description = "some description",
                logoUrl = "some logo url",
                termsConditionsUrl = "some terms and conditions")

        val DRIVER = Driver(
                firstName = "Michael",
                lastName = "Higgins",
                phoneNumber = "+441111111111",
                licenceNumber = "ZXZ151YTY",
                photoUrl = "https://karhoo.com/drivers/mydriver.png")

        val VEHICLE = Vehicle(
                vehicleClass = "MPV",
                description = "Renault Scenic (Black)",
                driver = DRIVER,
                vehicleLicencePlate = "123 XYZ")

        val TRIP_MEETING_POINT = Position(
                longitude = -0.1375291,
                latitude = 51.5086692)

        val MEETING_POINT = MeetingPoint(
                position = TRIP_MEETING_POINT,
                pickupType = PickupType.DEFAULT,
                instructions = "string")

        val SCHEDULED_DATE = getDate("2018-04-21T12:35:00Z")

        val TRIP = TripInfo(
                tripId = "b6a5f9dc-9066-4252-9013-be85dfa563bc",
                origin = TRIP_LOCATION_INFO,
                destination = TRIP_LOCATION_INFO,
                dateScheduled = SCHEDULED_DATE,
                flightNumber = "BA1326",
                tripState = TripStatus.REQUESTED,
                quote = PRICE,
                fleetInfo = FLEET_INFO,
                comments = "They are waiting by the green door at  Street 100",
                vehicle = VEHICLE,
                displayTripId = "A5TH-R27D",
                meetingPoint = MEETING_POINT)

        val TRIP_HISTORY = listOf(TRIP)

        val FARE_BREAKDOWN = FareBreakdown(
                currency = "EUR",
                total = 15)

        val FARE = Fare(
                state = "PENDING",
                breakdown = FARE_BREAKDOWN)

        val BLANK_TRIP_HISTORY = listOf<TripInfo>()

        val PAYMENT_TOKEN = BraintreeSDKToken(
                token = "njfdeilnvbflinvbiurnceernnvbrgtuverosa")

        val BLANK_PAYMENT_TOKEN = BraintreeSDKToken()

        val TRIP_STATE = TripState(TripStatus.NO_DRIVERS)

        const val QUOTE_LIST_ID = "129e51a-bc10-11e8-a821-0a580a0414db"

        val QUOTE = Quote(id = "someQuoteId",
                quoteType = QuoteType.ESTIMATED,
                quoteSource = QuoteSource.FLEET,
                price = ServerRobot.QUOTE_PRICE,
                fleet = ServerRobot.QUOTE_FLEET,
                pickupType = PickupType.CURBSIDE,
                vehicle = ServerRobot.QUOTE_VEHICLE)

        val QUOTE_LIST = QuoteList(
                id = QuoteId(QUOTE_LIST_ID),
                status = QuoteStatus.COMPLETED,
                validity = 10,
                categories = mapOf(
                        Pair("Saloon", emptyList()),
                        Pair("Taxi", emptyList()),
                        Pair("MPV", emptyList()),
                        Pair("Exec", listOf(QUOTE)),
                        Pair("Electric", emptyList()),
                        Pair("Moto", emptyList())))

        val QUOTE_LIST_EMPTY = QuoteList(
                id = QuoteId(QUOTE_LIST_ID),
                status = QuoteStatus.PROGRESSING,
                validity = 10,
                categories = mapOf(
                        Pair("Saloon", emptyList()),
                        Pair("Taxi", emptyList()),
                        Pair("MPV", emptyList()),
                        Pair("Exec", emptyList()),
                        Pair("Electric", emptyList()),
                        Pair("Moto", emptyList())))

        fun getDate(dateScheduled: String): Date {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm").apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            return formatter.parse(dateScheduled)
        }
    }
}