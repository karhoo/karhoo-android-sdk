package com.karhoo.sdk.api.datastore.user

import android.os.Parcelable
import com.karhoo.sdk.api.model.CardType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SavedPaymentInfo(val lastFour: String,
                            val cardType: CardType?) : Parcelable
