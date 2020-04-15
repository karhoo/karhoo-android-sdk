package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class PoiType constructor(val value: String) {

    @SerializedName("AIRPORT")
    AIRPORT("AIRPORT"),

    @SerializedName("NOT_SET", alternate = ["NOT_SET_DETAILS_TYPE"])
    NOT_SET("NOT_SET")

}
