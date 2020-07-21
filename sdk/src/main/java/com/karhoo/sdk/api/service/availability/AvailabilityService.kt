package com.karhoo.sdk.api.service.availability

import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.call.Call

@Deprecated("Availabilities endpoint is deprecated")
interface AvailabilityService {

    @Deprecated("Availabilities endpoint is deprecated")
    fun availability(quotesSearch: QuotesSearch): Call<Categories>

}
