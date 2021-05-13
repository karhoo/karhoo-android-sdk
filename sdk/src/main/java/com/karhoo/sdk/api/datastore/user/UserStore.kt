package com.karhoo.sdk.api.datastore.user

import com.karhoo.sdk.api.model.Provider
import com.karhoo.sdk.api.model.UserInfo

interface UserStore {

    val currentUser: UserInfo

    var paymentProvider: Provider?

    var savedPaymentInfo: SavedPaymentInfo?

    val isCurrentUserValid: Boolean

    fun clearSavedPaymentInfo()

    fun removeCurrentUser()

    fun addSavedPaymentObserver(onUserPaymentChangedListener: UserManager.OnUserPaymentChangedListener?)

}
