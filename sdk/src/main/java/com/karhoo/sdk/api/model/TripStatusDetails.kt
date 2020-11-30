package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class TripStatusDetails constructor(val value: String) {

    // User cancellation

    @SerializedName("DRIVER_DIDNT_SHOW_UP")
    DRIVER_DIDNT_SHOW_UP("DRIVER_DIDNT_SHOW_UP"),

    @SerializedName("ETA_TOO_LONG")
    ETA_TOO_LONG("ETA_TOO_LONG"),

    @SerializedName("DRIVER_IS_LATE")
    DRIVER_IS_LATE("DRIVER_IS_LATE"),

    @SerializedName("CAN_NOT_FIND_VEHICLE")
    CAN_NOT_FIND_VEHICLE("CAN_NOT_FIND_VEHICLE"),

    @SerializedName("NOT_NEEDED_ANYMORE")
    NOT_NEEDED_ANYMORE("NOT_NEEDED_ANYMORE"),

    @SerializedName("ASKED_BY_DRIVER_TO_CANCEL")
    ASKED_BY_DRIVER_TO_CANCEL("ASKED_BY_DRIVER_TO_CANCEL"),

    @SerializedName("FOUND_BETTER_PRICE")
    FOUND_BETTER_PRICE("FOUND_BETTER_PRICE"),

    @SerializedName("NOT_CLEAR_MEETING_INSTRUCTIONS")
    NOT_CLEAR_MEETING_INSTRUCTIONS("NOT_CLEAR_MEETING_INSTRUCTIONS"),

    @SerializedName("COULD_NOT_CONTACT_CARRIER")
    COULD_NOT_CONTACT_CARRIER("COULD_NOT_CONTACT_CARRIER"),

    @SerializedName("OTHER_USER_REASON")
    OTHER_USER_REASON("OTHER_USER_REASON"),

    // Driver cancellations

    @SerializedName("REQUESTED_BY_USER")
    REQUESTED_BY_USER("REQUESTED_BY_USER"),

    @SerializedName("PASSENGER_DIDNT_SHOW_UP")
    PASSENGER_DIDNT_SHOW_UP("PASSENGER_DIDNT_SHOW_UP"),

    @SerializedName("DRIVER_CANCELLED")
    DRIVER_CANCELLED("DRIVER_CANCELLED"),

    @SerializedName("SUPPLIER_CANCELLED")
    SUPPLIER_CANCELLED("SUPPLIER_CANCELLED"),

    @SerializedName("DISPATCH_CANCELLED")
    DISPATCH_CANCELLED("DISPATCH_CANCELLED"),

    @SerializedName("NO_AVAILABILITY_IN_THE_AREA")
    NO_AVAILABILITY_IN_THE_AREA("NO_AVAILABILITY_IN_THE_AREA"),

    @SerializedName("NO_FEE")
    NO_FEE("NO_FEE"),

    @SerializedName("OTHER_DISPATCH_REASON")
    OTHER_DISPATCH_REASON("OTHER_DISPATCH_REASON"),

    // Karhoo Cancellations

    @SerializedName("FRAUD")
    FRAUD("FRAUD"),

    @SerializedName("NO_AVAILABILITY")
    NO_AVAILABILITY("NO_AVAILABILITY"),

    @SerializedName("ASKED_BY_USER")
    ASKED_BY_USER("ASKED_BY_USER"),

    @SerializedName("ASKED_BY_DISPATCH")
    ASKED_BY_DISPATCH("ASKED_BY_DISPATCH"),

    @SerializedName("ASKED_BY_DRIVER")
    ASKED_BY_DRIVER("ASKED_BY_DRIVER"),

    @SerializedName("FAILURE")
    FAILURE("FAILURE"),

    @SerializedName("PREAUTH_FAILED")
    PREAUTH_FAILED("PREAUTH_FAILED"),

    @SerializedName("OTHER_KARHOO_REASON")
    OTHER_KARHOO_REASON("OTHER_KARHOO_REASON");
}