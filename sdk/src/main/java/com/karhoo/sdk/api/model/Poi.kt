package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class Poi constructor(val value: String) {

    @SerializedName("NOT_SET_POI_TYPE")
    NOT_SET("NOT_SET_POI_TYPE"),
    @SerializedName("ENRICHED")
    ENRICHED("ENRICHED"),
    @SerializedName("REGULATED")
    REGULATED("REGULATED"),
    @SerializedName("NEAREST")
    NEAREST("NEAREST")
}
