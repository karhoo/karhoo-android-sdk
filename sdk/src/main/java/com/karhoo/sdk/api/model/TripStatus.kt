package com.karhoo.sdk.api.model

import com.google.gson.annotations.SerializedName

enum class TripStatus constructor(val value: String) {

    @SerializedName("REQUESTED")
    REQUESTED("REQUESTED"),
    @SerializedName("CONFIRMED")
    CONFIRMED("CONFIRMED"),
    @SerializedName("ARRIVED")
    ARRIVED("ARRIVED"),
    @SerializedName("POB")
    PASSENGER_ON_BOARD("POB"),
    @SerializedName("DRIVER_EN_ROUTE")
    DRIVER_EN_ROUTE("DRIVER_EN_ROUTE"),
    @SerializedName("NO_DRIVERS_AVAILABLE")
    NO_DRIVERS("NO_DRIVERS_AVAILABLE"),
    @SerializedName(value = "COMPLETED", alternate = ["RIDE_COMPLETED"])
    COMPLETED("COMPLETED"),
    @SerializedName("BOOKER_CANCELLED")
    CANCELLED_BY_USER("BOOKER_CANCELLED"),
    @SerializedName("DRIVER_CANCELLED")
    CANCELLED_BY_DISPATCH("DRIVER_CANCELLED"),
    @SerializedName("KARHOO_CANCELLED")
    CANCELLED_BY_KARHOO("KARHOO_CANCELLED"),
    @SerializedName("INCOMPLETE")
    INCOMPLETE("INCOMPLETE");

    companion object {
        fun tripEnded(tripState: TripStatus?): Boolean {
            return when (tripState) {
                COMPLETED,
                CANCELLED_BY_USER,
                CANCELLED_BY_DISPATCH,
                NO_DRIVERS,
                CANCELLED_BY_KARHOO ->
                    true
                else -> false
            }
        }
    }
}
