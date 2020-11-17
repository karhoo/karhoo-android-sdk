package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class QuoteSource constructor(private val source: String) {

    @SerializedName("FLEET")
    FLEET("FLEET"),
    @SerializedName("MARKET")
    MARKET("MARKET");

    override fun toString(): String {
        val sb = StringBuilder("QuoteSource{")
        sb.append("source='").append(source).append('\'')
        sb.append('}')
        return sb.toString()
    }

}
