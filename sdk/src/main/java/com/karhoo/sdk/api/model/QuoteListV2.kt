package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteListV2(val categories: Map<String, List<QuoteV2>>,
                       val id: QuoteId) : Parcelable
