package com.karhoo.sdk.api.datastore.user

import com.karhoo.sdk.api.model.LoyaltyStatus
import com.karhoo.sdk.api.model.Provider
import com.karhoo.sdk.api.model.UserInfo

interface UserManager {

    val user: UserInfo

    val isUserStillValid: Boolean

    var savedPaymentInfo: SavedPaymentInfo?

    var paymentProvider: Provider?

    var loyaltyStatus: LoyaltyStatus?

    fun saveUser(userInfo: UserInfo)

    fun deleteUser()

    fun deleteSavedPaymentInfo()

    fun addSavedPaymentObserver(userInfoListener: OnUserPaymentChangedListener?)

    interface OnUserPaymentChangedListener {

        fun onSavedPaymentInfoChanged(userPaymentInfo: SavedPaymentInfo?)

    }

}
