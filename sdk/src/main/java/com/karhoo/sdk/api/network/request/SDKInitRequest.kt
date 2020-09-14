package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SDKInitRequest(@SerializedName("organisation_id") val organisationId: String,
                          @SerializedName("currency") val currency: String) : Parcelable
