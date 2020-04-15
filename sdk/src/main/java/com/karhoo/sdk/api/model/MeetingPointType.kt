package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class MeetingPointType(val type: String) {

    @SerializedName("NOT_SET_DETAILS_TYPE")
    NOT_SET_DETAILS_TYPE("NOT_SET_DETAILS_TYPE"),
    @SerializedName("AIRPORT")
    AIRPORT("AIRPORT"),
    @SerializedName("TRAIN_STATION")
    TRAIN_STATION("TRAIN_STATION"),
    @SerializedName("DEFAULT")
    NOT_SET("DEFAULT"),
    @SerializedName("DEFAULT")
    DEFAULT("DEFAULT"),
    @SerializedName("PICK_UP")
    PICK_UP("PICK_UP"),
    @SerializedName("DROP_OFF")
    DROP_OFF("DROP_OFF"),
    @SerializedName("MEET_AND_GREET")
    MEET_AND_GREET("MEET_AND_GREET"),
    @SerializedName("CURB_SIDE")
    CURB_SIDE("CURB_SIDE"),
    @SerializedName("STAND_BY")
    STAND_BY("STAND_BY")
}
