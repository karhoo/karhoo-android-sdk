package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteList(val categories: Map<String, List<Quote>>,
                     val id: QuoteId,
                     val status: String?,
                     val validity: Int) : Parcelable
