package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotesV2Request(@SerializedName("origin") val origin: QuoteRequestPoint,
                           @SerializedName("destination") val destination: QuoteRequestPoint,
                           @SerializedName("local_time_of_pickup") val dateScheduled: String?) :
        Parcelable