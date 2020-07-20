package com.karhoo.sdk.api.service.availability

import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.call.Call
import javax.inject.Inject

@Deprecated("Availabilities endpoint is deprecated")
class KarhooAvailabilityService @Inject internal constructor(private val availabilityInteractor: AvailabilityInteractor) : AvailabilityService {

    override fun availability(quotesSearch: QuotesSearch): Call<Categories> {
        availabilityInteractor.quotesSearch = quotesSearch
        return availabilityInteractor
    }
}
