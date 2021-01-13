package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class QuoteStatus constructor(val value: String) {

    @SerializedName("PROGRESSING")
    PROGRESSING("PROGRESSING"),

    @SerializedName("COMPLETED")
    COMPLETED("COMPLETED"),

    GENERIC("GENERIC");

    override fun toString(): String {
        val sb = StringBuilder("QuoteStatus{")
        sb.append("status='").append(value).append('\'')
        sb.append('}')
        return sb.toString()
    }
}
