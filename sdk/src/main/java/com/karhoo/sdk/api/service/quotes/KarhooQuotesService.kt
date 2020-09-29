package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.service.availability.AvailabilityService
import com.karhoo.sdk.call.PollCall
import javax.inject.Inject

class KarhooQuotesService : QuotesService {

    @Inject
    @Deprecated("Availabilities endpoint is deprecated")
    internal lateinit var availabilityService: AvailabilityService

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun quotes(quotesSearch: QuotesSearch): PollCall<QuoteList> = QuotesInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate).apply {
        this.quotesSearch = quotesSearch
    }
}
