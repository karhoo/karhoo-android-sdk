package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class TripType constructor(val value: String) {

    @SerializedName("BOTH")
    BOTH("BOTH"),

    @SerializedName("PREBOOK")
    PREBOOK("PREBOOK"),

    @SerializedName("ASAP")
    ASAP("ASAP");
}
