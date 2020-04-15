package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UISetting(val uiSetting: Map<String, UIConfig>) : Parcelable
