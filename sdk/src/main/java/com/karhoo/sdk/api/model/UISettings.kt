package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UISettings(val uiSetting: Map<String, UISetting>) : Parcelable
