package com.karhoo.sdk.api.datastore.user

import com.karhoo.sdk.api.model.UserInfo

interface UserStore {

    val currentUser: UserInfo

    var savedPaymentInfo: SavedPaymentInfo?

    val isCurrentUserValid: Boolean

    fun removeCurrentUser()

    fun addSavedPaymentObserver(onUserPaymentChangedListener: UserManager.OnUserPaymentChangedListener?)

}