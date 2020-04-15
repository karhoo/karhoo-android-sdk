package com.karhoo.sdk.api.network.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UIConfigRequest(val viewId: String) : Parcelable
