package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class CardType constructor(val value: String) {

    @SerializedName("Visa")
    VISA("Visa"),

    @SerializedName("MasterCard")
    MASTERCARD("MasterCard"),

    @SerializedName("American Express")
    AMEX("American Express"),

    NOT_SET("DEFAULT");

    companion object {
        fun fromString(string: String): CardType? = when (string.toLowerCase()) {
            "visa" -> VISA
            "mc" -> MASTERCARD
            "mastercard" -> MASTERCARD
            "american express" -> AMEX
            "default" -> NOT_SET
            else -> null
        }
    }

}
