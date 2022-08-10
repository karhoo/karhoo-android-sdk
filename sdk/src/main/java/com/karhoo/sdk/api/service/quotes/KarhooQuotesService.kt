package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.Coverage
import com.karhoo.sdk.api.model.Quote
import com.karhoo.sdk.api.model.QuoteId
import com.karhoo.sdk.api.model.QuoteList
import com.karhoo.sdk.api.model.QuotesSearch
import com.karhoo.sdk.api.model.VehicleMappings
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.CoverageRequest
import com.karhoo.sdk.call.Call
import com.karhoo.sdk.call.PollCall
import javax.inject.Inject

class KarhooQuotesService : QuotesService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun quotes(quotesSearch: QuotesSearch): PollCall<QuoteList> = QuotesInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate).apply {
        this.quotesSearch = quotesSearch
    }

    override fun quotes(quotesSearch: QuotesSearch, locale: String?): PollCall<QuoteList> =
        QuotesInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate
        ).apply {
            this.quotesSearch = quotesSearch
            this.locale = locale
        }

    override fun checkCoverage(coverageRequest: CoverageRequest): Call<Coverage> =
        CheckCoverageInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate
        ).apply {
            this.coverageRequest = coverageRequest
        }

    override fun verifyQuotes(quoteId: QuoteId): Call<Quote> =
        VerifyQuoteInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate
        ).apply {
            this.quoteIdRequest = quoteId
        }

    override fun getVehicleMappings(): Call<VehicleMappings> {
        return VehicleMappingsInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate
        )
    }
}
