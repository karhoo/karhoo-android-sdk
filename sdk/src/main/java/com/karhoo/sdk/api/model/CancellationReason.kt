package com.karhoo.sdk.api.model

enum class CancellationReason(val reason: String) {

    OTHER_USER_REASON("OTHER _USER_REASON"),
    DRIVER_DIDNT_SHOW_UP("DRIVER_DIDNT_SHOW_UP"),
    ETA_TOO_LONG("ETA_TOO_LONG"),
    DRIVER_IS_LATE("DRIVER_IS_LATE"),
    CAN_NOT_FIND_VEHICLE("CAN_NOT_FIND_VEHICLE"),
    NOT_NEEDED_ANYMORE("NOT_NEEDED_ANYMORE"),
    ASKED_BY_DRIVER_TO_CANCEL("ASKED_BY_DRIVER_TO_CANCEL"),
    FOUND_BETTER_PRICE("FOUND_BETTER_PRICE"),
    NOT_CLEAR_MEETING_INSTRUCTIONS("NOT_CLEAR_MEETING_INSTRUCTIONS"),
    COULD_NOT_CONTACT_CARRIER("COULD_NOT_CONTACT_CARRIER")

}
