package com.karhoo.sdk.analytics

enum class Event constructor(val value: String) {

    LOGGED_IN("user_logged_in"),
    LOGGED_OUT("user_logged_out"),
    USER_REGISTER_STARTED("user_registration_started"),
    USER_REGISTER_COMPLETE("user_registration_completed"),
    REJECT_LOCATION_SERVICES("location_services_rejected"),
    LOADED_USERS_LOCATION("loaded_user_location"),
    MORE_SUPPLIERS("more_suppliers"),
    APP_OPENED("app_opened"),
    APP_CLOSED("app_closed"),
    APP_BACKGROUND("app_backgrounded"),
    BOOKING_WITH_CALLBACK("booking_with_callback"),
    TRIP_STATE_CHANGED("state_change_displayed"),
    USER_CANCEL_TRIP("trip_cancellation_attempted_by_user"),
    AMOUNT_ADDRESSES("pickup_address_displayed"),
    PICKUP_SELECTED("pickup_address_selected"),
    DESTINATION_SELECTED("destination_address_displayed"),
    DESTINATION_PRESSED("destination_field_pressed"),
    REVERSE_GEO("pickup_reverse_geocode_requested"),
    REVERSE_GEO_RESPONSE("pickup_reverse_geocode_responded"),
    CURRENT_LOCATION_PRESSED("current_location_pressed"),
    PREBOOK_SET("prebook_time_set"),
    VEHICLE_SELECTED("vehicle_type_selected"),
    FLEET_LIST_SHOWN("fleet_list_shown"),
    FLEET_SORTED("fleets_sorted"),
    PREBOOK_OPENED("prebook_picker_opened"),
    PREBOOK_CONFIRMATION("prebook_confirmation"),
    CARD_REGISTERED_SUCCESSFULLY("user_card_registered"),
    CARD_REGISTERED_FAILED("user_card_registration_failed"),
    TERMS_REVIEWED("user_terms_reviewed"),
    USER_CALLED_DRIVER("user_called_driver"),
    USER_CALLED_FLEET("user_called_fleet"),
    USER_ENTERED_ADDRESS("user_entered_pickup_address"),
    TRACK_RIDE("track_ride"),
    USER_POSITION_CHANGED("user_position_changed"),
    TRIP_RATING_SUBMITTED("trip_rating_submitted"),
    ADDITIONAL_FEEDBACK_SUBMITTED("additional_feedback_submitted"),
    USER_PROFILE_EDIT_PRESSED("user_profile_edit_pressed"),
    USER_PROFILE_DISCARD_PRESSED("user_profile_discard_pressed"),
    USER_PROFILE_SAVE_PRESSED("user_profile_save_pressed"),
    USER_PROFILE_UPDATE_SUCCESS("user_profile_update_success"),
    USER_PROFILE_UPDATE_FAILED("user_profile_update_failed"),

    //PAYMENT
    CARD_AUTHORISATION_SUCCESS("card_auth_success"),
    CARD_AUTHORISATION_FAILURE("card_auth_failure"),
    PAYMENT_FAILED("payment_failed"),

    //LOYALTY
    LOYALTY_STATUS_REQUESTED("loyalty_status_requested"),
    LOYALTY_PREAUTH_FAILED("loyalty_preauth_failed"),
    LOYALTY_PREAUTH_SUCCESS("loyalty_preauth_success"),

    //CHECKOUT
    CHECKOUT_SCREEN("checkout_screen"),

    //BOOKING
    BOOKING_REQUESTED("booking_requested"),
    BOOKING_SUCCESS("booking_payment_success"),
    BOOKING_FAILURE("booking_payment_failed"),

    REQUEST_ERROR("request_error")

}
