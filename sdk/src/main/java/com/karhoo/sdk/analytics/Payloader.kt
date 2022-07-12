package com.karhoo.sdk.analytics

import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.LoyaltyProgramme
import com.karhoo.sdk.api.model.LoyaltyBalance
import com.karhoo.sdk.api.model.LoyaltyStatus
import java.util.HashMap
import java.util.Date

class Payloader internal constructor(builder: Builder) {

    val payload: MutableMap<String, Any>

    init {
        this.payload = builder.payload
    }

    internal fun setSessionTokens(deviceId: String, sessionId: String) {
        payload[PERM_ID] = deviceId
        payload[SESSION_ID] = sessionId
    }

    internal fun setUser(user: UserInfo?) {
        if (user != null) {
            payload[USER_ID] = user.userId
        }
    }

    internal fun setUserLatLng(position: Position?) {
        position?.let {
            payload[USER_LAT] = it.latitude
            payload[USER_LNG] = it.longitude
        }
    }

    internal fun setGuestMode(isGuestMode: Boolean) {
        payload[GUEST_MODE] = isGuestMode
    }

    class Builder private constructor() {

        val payload: MutableMap<String, Any> = HashMap()

        fun addUserDetails(user: UserInfo): Builder {
            payload[USER_ID] = user.userId
            return this
        }

        fun addTripRating(tripId: String, rating: Float): Builder {
            payload[TRIP_ID] = tripId
            payload[TRIP_RATING_SUBMITTED] = rating
            return this
        }

        fun addSource(source: String): Builder {
            payload[SOURCE] = source
            return this
        }

        fun additionalFeedback(additionalFeedback: List<MutableMap<String, Any>>): Builder {
            payload[ADDITIONAL_FEEDBACK] = additionalFeedback
            return this
        }

        fun bookingRequested(
            battery: Float,
            connectionType: String,
            tripDetails: TripInfo,
            outboundTripId: String?,
            correlationId: String?,
            quoteId: String?
        ): Builder {
            payload[BATTERY_LIFE] = battery
            payload[NETWORK_TYPE] = connectionType
            tripDetails.quote?.let {
                payload[PRICE] = it.total
            }
            tripDetails.destination?.position?.let {
                payload[DESTINATION_LAT] = it.latitude
                payload[DESTINATION_LNG] = it.longitude
            }
            tripDetails.destination?.let {
                payload[DESTINATION_ADDRESS] = it.displayAddress
            }
            payload[IS_PREBOOK] = tripDetails.dateScheduled != null
            if (outboundTripId != null && outboundTripId.isNotEmpty()) {
                payload[OUTBOUND_TRIP_ID] = outboundTripId
            }
            quoteId?.let {
                payload[QUOTE_ID] = it
            }
            correlationId?.let {
                payload[CORRELATION_ID] = it
            }

            return this
        }

        fun paymentSuccess(
            tripDetails: TripInfo,
            correlationId: String?,
            quote: Quote?
        ): Builder {
            quote?.id?.let {
                payload[QUOTE_ID] = it
            }
            correlationId?.let {
                payload[CORRELATION_ID] = it
            }
            payload[TRIP_ID] = tripDetails.tripId

            return this
        }

        fun paymentFailure(
            correlationId: String?,
            quoteId: String?,
            errorMessage: String,
            lastFourDigits: String,
            date: Date,
            amount: Int,
            currency: String
        ): Builder {
            quoteId?.let {
                payload[QUOTE_ID] = it
            }
            correlationId?.let {
                payload[CORRELATION_ID] = it
            }
            payload[PAYMENT_ERROR_MESSAGE] = errorMessage
            payload[PAYMENT_CARD_LAST_FOUR_DIGITS] = lastFourDigits
            payload[PAYMENT_AMOUNT] = amount
            payload[PAYMENT_CURRENCY] = currency
            payload[PAYMENT_DATE] = date.toString()

            return this
        }

        fun addTripState(tripState: String): Builder {
            payload[TRIP_STATE] = tripState
            return this
        }

        fun addCurrentEta(eta: Int): Builder {
            payload[ETA_DISPLAYED] = eta
            return this
        }

        fun addTimeToArrival(eta: Int): Builder {
            payload[TTA_DISPLAYED] = eta
            return this
        }

        fun displayedAddresses(amount: Int): Builder {
            payload[AMOUNT_ADDRESSES] = amount
            return this
        }

        fun addressSelected(displayAddress: String, addressPositionInList: Int): Builder {
            payload[ADDRESS] = displayAddress
            payload[ADDRESS_POSITION_IN_LIST] = addressPositionInList
            return this
        }

        fun reverseGeoResponse(locationInfo: LocationInfo): Builder {
            if (locationInfo.position != null) {
                payload[PICKUP_LAT] = locationInfo.position.latitude
                payload[PICKUP_LONG] = locationInfo.position.longitude
            }
            payload[PICKUP_ADDRESS] = locationInfo.displayAddress
            return this
        }

        fun prebookSet(date: String?): Builder {
            payload[PREBOOK_SET] = date.orEmpty()
            return this
        }

        fun vehicleSelected(vehicle: String?, quoteId: String?): Builder {
            payload[VEHICLE_TYPE] = vehicle.orEmpty()
            payload[QUOTE_LIST_ID] = quoteId.orEmpty()
            return this
        }

        fun fleetsShown(amount: Int, quoteId: String?): Builder {
            payload[SCREEN_ROWS] = amount
            payload[QUOTE_LIST_ID] = quoteId.orEmpty()
            return this
        }

        fun fleetsSorted(sortType: String?, quoteId: String?): Builder {
            payload[VEHICLE_TYPE] = sortType.orEmpty()
            payload[QUOTE_LIST_ID] = quoteId.orEmpty()
            return this
        }

        fun paymentFailed(
            errorMessage: String,
            lastFourDigits: String,
            date: Date,
            amount: Int,
            currency: String
        ): Builder {
            payload[PAYMENT_ERROR_MESSAGE] = errorMessage
            payload[PAYMENT_CARD_LAST_FOUR_DIGITS] = lastFourDigits
            payload[PAYMENT_AMOUNT] = amount
            payload[PAYMENT_CURRENCY] = currency
            payload[PAYMENT_DATE] = date.toString()
            return this
        }

        fun cardAuthorisationFailed(
            errorMessage: String,
            lastFourDigits: String,
            date: Date,
            amount: Int,
            currency: String
        ): Builder {
            payload[PAYMENT_ERROR_MESSAGE] = errorMessage
            payload[PAYMENT_CARD_LAST_FOUR_DIGITS] = lastFourDigits
            payload[PAYMENT_AMOUNT] = amount
            payload[PAYMENT_CURRENCY] = currency
            payload[PAYMENT_DATE] = date.toString()
            return this
        }

        fun checkoutOpened(quoteId: String?): Builder {
            quoteId?.let {
                payload[QUOTE_ID] = it
            }

            return this
        }

        fun cardAuthorisationSuccess(quoteId: String?): Builder {
            quoteId?.let {
                payload[QUOTE_ID] = it
            }

            return this
        }

        fun loyaltyStatusRequested(
            quoteId: Quote?,
            loyaltyStatus: LoyaltyStatus?,
            loyaltyBalance: LoyaltyBalance?,
            loyaltyProgramme: LoyaltyProgramme?,
            correlationId: String?,
            slug: String?
        ): Builder {
            payload[LOYALTY_ENABLED] =
                loyaltyStatus?.canBurn == false && loyaltyStatus.canEarn == false
            payload[LOYALTY_STATUS_SUCCESS] = loyaltyStatus != null
            payload[LOYALTY_STATUS_CAN_BURN] = loyaltyStatus?.canBurn ?: false
            payload[LOYALTY_STATUS_CAN_EARN] = loyaltyStatus?.canEarn ?: false
            payload[LOYALTY_STATUS_BALANCE] = loyaltyBalance?.points ?: 0

            quoteId?.id?.let {
                payload[QUOTE_ID] = it
            }
            loyaltyProgramme?.name?.let {
                payload[LOYALTY_NAME] = it
            }
            correlationId?.let {
                payload[CORRELATION_ID] = it
            }
            slug?.let {
                payload[LOYALTY_STATUS_ERROR_SLUG] = it
            }

            return this
        }

        fun loyaltyPreAuthFailure(
            quoteId: String?,
            canBurn: Boolean,
            correlationId: String?,
            slug: String?
        ): Builder {
            quoteId?.let {
                payload[QUOTE_ID] = it
            }
            correlationId?.let {
                payload[CORRELATION_ID] = it
            }
            slug?.let {
                payload[LOYALTY_STATUS_ERROR_SLUG] = it
            }

            payload[LOYALTY_PREAUTH_TYPE] = canBurn

            return this
        }

        fun loyaltyPreAuthSuccess(
            quoteId: String?,
            canBurn: Boolean,
            correlationId: String?
        ): Builder {
            quoteId?.let {
                payload[QUOTE_ID] = it
            }
            correlationId?.let {
                payload[CORRELATION_ID] = it
            }

            payload[LOYALTY_PREAUTH_TYPE] = canBurn

            return this
        }

        fun tripId(tripId: String?): Builder {
            payload[TRIP_ID] = tripId.orEmpty()
            return this
        }

        fun userEnteredAddressSearch(search: String?): Builder {
            payload[TEXT_ENTERED_ADDRESS_SEARCH] = search.orEmpty()
            return this
        }

        fun requestError(error: String?, url: String?): Builder {
            payload[REQUEST_ERROR] = error.orEmpty()
            payload[REQUEST_URL] = url.orEmpty()
            return this
        }

        fun build(): Payloader {
            return Payloader(this)
        }

        companion object {

            val builder: Builder
                get() = Builder()
        }

    }

    companion object {
        private const val USER_ID = "user_id"
        private const val PERM_ID = "perm_id"
        private const val TRIP_ID = "trip_id"
        private const val SESSION_ID = "session_id"
        private const val USER_LAT = "user_location_latitude"
        private const val USER_LNG = "user_location_longitude"
        private const val USER_MANUAL_PICKUP_ADDRESS = "text_entered_by_user"
        private const val BATTERY_LIFE = "battery_life"
        private const val NETWORK_TYPE = "network_type"
        private const val PRICE = "highPrice"
        private const val DESTINATION_LAT = "destination_latitude"
        private const val DESTINATION_LNG = "destination_longitude"
        private const val DESTINATION_ADDRESS = "destination_address"
        private const val IS_PREBOOK = "is_prebook"
        private const val TRIP_STATE = "trip_state"
        private const val ETA_DISPLAYED = "eta_displayed"
        private const val TTA_DISPLAYED = "deta_displayed"
        private const val AMOUNT_ADDRESSES = "count_address_options"
        private const val ADDRESS = "address"
        private const val PREVIOUS_ADDRESS = "previous_address_selected_"
        private const val PICKUP_LAT = "previous_address_selected_"
        private const val PICKUP_LONG = "previous_address_selected_"
        private const val PICKUP_ADDRESS = "previous_address_selected_"
        private const val VEHICLE_TYPE = "vehicle_type_selected"
        private const val QUOTE_LIST_ID = "quote_list_id"
        private const val QUOTE_ID = "quote_id"
        private const val PREBOOK_SET = "prebook_time_set"
        private const val SCREEN_ROWS = "screen_rows_default"
        private const val TEXT_ENTERED_ADDRESS_SEARCH = "text_entered_by_user"
        private const val OUTBOUND_TRIP_ID = "outbound_trip_id"
        private const val ADDRESS_POSITION_IN_LIST = "address_position_in_list"
        private const val TRIP_RATING_SUBMITTED = "trip_rating_submitted"
        private const val SOURCE = "source"
        private const val ADDITIONAL_FEEDBACK = "additional_feedback"
        private const val REQUEST_ERROR = "request_error"
        private const val REQUEST_URL = "request_url"
        private const val GUEST_MODE = "guest_mode"
        private const val PAYMENT_DATE = "date"
        private const val PAYMENT_ERROR_MESSAGE = "message"
        private const val PAYMENT_CARD_LAST_FOUR_DIGITS = "card_last_4_digits"
        private const val PAYMENT_AMOUNT = "amount"
        private const val PAYMENT_CURRENCY = "currency"

        private const val LOYALTY_ENABLED = "loyalty_enabled"
        private const val LOYALTY_NAME = "loyalty_name"
        private const val LOYALTY_STATUS_SUCCESS = "loyalty_status_success"
        private const val LOYALTY_STATUS_CAN_EARN = "loyalty_status_can_earn"
        private const val LOYALTY_STATUS_CAN_BURN = "loyalty_status_can_burn"
        private const val LOYALTY_STATUS_BALANCE = "loyalty_status_balance"
        private const val LOYALTY_STATUS_ERROR_SLUG = "loyalty_status_error_slug"
        private const val LOYALTY_STATUS_ERROR_MESSAGE = "loyalty_status_error_message"
        private const val CORRELATION_ID = "correlation_id"

        private const val LOYALTY_PREAUTH_TYPE = "loyalty_preauth_type"
        private const val LOYALTY_PREAUTH_ERROR_SLUG = "loyalty_preauth_error_slug"

    }
}
