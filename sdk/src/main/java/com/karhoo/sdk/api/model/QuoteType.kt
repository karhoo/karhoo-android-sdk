package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class QuoteType {

    @SerializedName("FIXED")
    FIXED,
    @SerializedName("ESTIMATED")
    ESTIMATED,
    @SerializedName("METERED")
    METERED

}
