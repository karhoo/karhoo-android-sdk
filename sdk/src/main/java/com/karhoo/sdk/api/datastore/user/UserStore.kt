package com.karhoo.sdk.api.datastore.user

import com.karhoo.sdk.api.model.LoyaltyStatus
import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.Provider
import com.karhoo.sdk.api.model.UserInfo

interface UserStore {

    val currentUser: UserInfo

    var paymentProvider: PaymentProvider?

    var savedPaymentInfo: SavedPaymentInfo?

    val isCurrentUserValid: Boolean

    var loyaltyStatus: LoyaltyStatus?

    fun clearSavedPaymentInfo()

    fun removeCurrentUser()

    fun addSavedPaymentObserver(onUserPaymentChangedListener: UserManager.OnUserPaymentChangedListener?)

}
