package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.model.BookingFee
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.request.TripCancellation
import com.karhoo.sdk.api.network.request.TripSearch
import com.karhoo.sdk.call.Call
import com.karhoo.sdk.call.PollCall

interface TripsService {

    fun book(tripBooking: TripBooking): Call<TripInfo>
    
    fun trackTrip(tripIdentifier: String): PollCall<TripInfo>

    fun cancel(tripCancellation: TripCancellation): Call<Void>

    fun search(tripSearch: TripSearch): Call<List<TripInfo>>

    fun status(tripId: String): PollCall<TripState>

    fun cancellationFee(feeIdentifier: String): Call<BookingFee>

}
