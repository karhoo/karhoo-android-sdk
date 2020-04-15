package com.karhoo.sdk.api.datastore.user

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.UserInfo
import javax.inject.Inject

class KarhooUserStore : UserStore {

    @Inject
    internal lateinit var userManager: UserManager

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    override fun removeCurrentUser() {
        userManager.deleteUser()
        credentialsManager.deleteCredentials()
    }

    override fun addSavedPaymentObserver(onUserPaymentChangedListener: UserManager.OnUserPaymentChangedListener?) {
        onUserPaymentChangedListener?.let {
            userManager.addSavedPaymentObserver(onUserPaymentChangedListener)
        }
    }

    override val currentUser: UserInfo
        get() = userManager.user

    override val isCurrentUserValid: Boolean
        get() = userManager.isUserStillValid

    override var savedPaymentInfo: SavedPaymentInfo?
        get() = userManager.savedPaymentInfo
        set(value) {
            value?.let { userManager.savedPaymentInfo = value }
        }

}
