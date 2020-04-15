package com.karhoo.sdk.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class KarhooInternalError(@SerializedName("code") val code: String = "",
                                        @SerializedName("message") val message: String = "") : Parcelable