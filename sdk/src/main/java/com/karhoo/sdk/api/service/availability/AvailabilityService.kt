package com.karhoo.sdk.api.service.availability

import com.karhoo.sdk.api.model.Categories
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.call.Call

interface AvailabilityService {

    fun availability(quotesSearch: QuotesSearch): Call<Categories>

}
