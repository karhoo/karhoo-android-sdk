package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class QuoteStatus constructor(private val status: String) {

    @SerializedName("PROGRESSING")
    PROGRESSING("PROGRESSING"),
    @SerializedName("COMPLETED")
    COMPLETED("COMPLETED"),

    GENERIC("GENERIC");

    override fun toString(): String {
        val sb = StringBuilder("QuoteStatus{")
        sb.append("status='").append(status).append('\'')
        sb.append('}')
        return sb.toString()
    }
}
