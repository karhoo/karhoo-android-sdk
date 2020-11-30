package com.karhoo.sdk.api.service.trips

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.BookingFee
import com.karhoo.sdk.api.model.TripInfo
import com.karhoo.sdk.api.model.TripState
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.TripBooking
import com.karhoo.sdk.api.network.request.TripCancellation
import com.karhoo.sdk.api.network.request.TripSearch
import com.karhoo.sdk.call.Call
import com.karhoo.sdk.call.PollCall
import javax.inject.Inject

class KarhooTripsService : TripsService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun book(tripBooking: TripBooking): Call<TripInfo> = BookTripInteractor(credentialsManager, apiTemplate).apply {
        this.tripBooking = tripBooking
    }

    override fun trackTrip(tripIdentifier: String): PollCall<TripInfo> = MonitorTripInteractor(credentialsManager, apiTemplate).apply {
        this.tripIdentifier = tripIdentifier
    }

    override fun cancel(tripCancellation: TripCancellation): Call<Void> = CancelTripInteractor(credentialsManager, apiTemplate).apply {
        this.tripCancellation = tripCancellation
    }

    override fun search(tripSearch: TripSearch): Call<List<TripInfo>> = TripListInteractor(credentialsManager, apiTemplate).apply {
        this.tripHistory = tripSearch
    }

    override fun status(tripId: String): PollCall<TripState> = TripStateInteractor(credentialsManager, apiTemplate).apply {
        this.tripIdentifier = tripId
    }

    override fun cancellationFee(feeIdentifier: String): Call<BookingFee> = CancellationFeeInteractor(credentialsManager, apiTemplate).apply {
        this.feeIdentifier = feeIdentifier
    }
}
