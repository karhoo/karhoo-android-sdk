package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeatureFlag(val version: String, val flags: Map<String, Boolean>) : Parcelable
