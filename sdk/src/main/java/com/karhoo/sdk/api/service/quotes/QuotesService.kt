package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuoteListV2
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.call.PollCall

interface QuotesService {

    fun quotesV2(quotesSearch: QuotesSearch): PollCall<QuoteListV2>
}
