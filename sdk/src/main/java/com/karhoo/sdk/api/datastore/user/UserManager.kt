package com.karhoo.sdk.api.datastore.user

import com.karhoo.sdk.api.model.PaymentProvider
import com.karhoo.sdk.api.model.Provider
import com.karhoo.sdk.api.model.UserInfo

interface UserManager {

    val user: UserInfo

    val isUserStillValid: Boolean

    var savedPaymentInfo: SavedPaymentInfo?

    var paymentProvider: Provider?

    fun saveUser(userInfo: UserInfo)

    fun deleteUser()

    fun addSavedPaymentObserver(userInfoListener: OnUserPaymentChangedListener?)

    interface OnUserPaymentChangedListener {

        fun onSavedPaymentInfoChanged(userPaymentInfo: SavedPaymentInfo?)

    }

}
