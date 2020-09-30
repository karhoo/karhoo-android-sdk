package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.call.PollCall

interface QuotesService {

    fun quotes(quotesSearch: QuotesSearch): PollCall<QuoteList>
}
