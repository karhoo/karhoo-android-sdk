package com.karhoo.sdk.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeatureFlags(val featureFlags: List<FeatureFlag>) : Parcelable