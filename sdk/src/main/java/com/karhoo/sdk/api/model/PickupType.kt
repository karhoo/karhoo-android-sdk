package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class PickupType constructor(val value: Int) {

    // The driver is expected to wait by the curb.
    @SerializedName("CURBSIDE")
    CURBSIDE(0),
    // The driver should meet the passenger in person at a predetermined meeting point.
    @SerializedName("MEET_AND_GREET")
    MEET_AND_GREET(1),
    // The driver should wait for the passenger, there may be a long wait time.
    @SerializedName("STANDBY")
    STANDBY(2),
    //Default type is set
    @SerializedName("DEFAULT")
    DEFAULT(3),
    //No meeting point is set
    @SerializedName("NOT_SET")
    NOT_SET(4)

}
