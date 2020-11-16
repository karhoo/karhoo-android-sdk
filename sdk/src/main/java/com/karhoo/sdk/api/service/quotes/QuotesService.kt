package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.model.Coverage
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.network.request.CoverageRequest
import com.karhoo.sdk.call.Call
import com.karhoo.sdk.call.PollCall

interface QuotesService {

    fun quotes(quotesSearch: QuotesSearch): PollCall<QuoteList>

    fun checkCoverage(coverageRequest: CoverageRequest): Call<Coverage>

    fun verifyQuotes(quoteId: QuoteId): Call<Quote>
}
