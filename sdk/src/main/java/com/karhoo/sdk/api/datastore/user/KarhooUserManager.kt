package com.karhoo.sdk.api.datastore.user

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.model.CardType
import com.karhoo.sdk.api.model.LoyaltyProgramme
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.Provider
import com.karhoo.sdk.api.model.UserInfo

class KarhooUserManager(private val sharedPreferences: SharedPreferences,
                        private val analytics: Analytics) : UserManager {

    val gson: Gson by lazy { Gson() }

    private var userPaymentObservers = mutableSetOf<UserManager.OnUserPaymentChangedListener?>()

    override fun addSavedPaymentObserver(userInfoListener: UserManager.OnUserPaymentChangedListener?) {
        userPaymentObservers.add(userInfoListener)
        userInfoListener?.onSavedPaymentInfoChanged(savedPaymentInfo)
    }

    override val user: UserInfo
        get() {
            return UserInfo(
                    userId = sharedPreferences.getString(USER_ID, "").orEmpty(),
                    firstName = sharedPreferences.getString(FIRST_NAME, "").orEmpty(),
                    lastName = sharedPreferences.getString(LAST_NAME, "").orEmpty(),
                    email = sharedPreferences.getString(EMAIL, "").orEmpty(),
                    phoneNumber = sharedPreferences.getString(MOBILE_NUMBER, "").orEmpty(),
                    locale = sharedPreferences.getString(LOCALE, "").orEmpty(),
                    organisations = getOrgsForUser())
        }

    override val isUserStillValid: Boolean
        get() = if (sharedPreferences.getString(EMAIL, "").orEmpty().isEmpty() &&
                sharedPreferences.getString(MOBILE_NUMBER, "").orEmpty().isEmpty()) {
            false
        } else {
            gson.toJson(user.organisations)
            analytics.userInfo = UserInfo(
                    userId = sharedPreferences.getString(USER_ID, "").orEmpty(),
                    firstName = sharedPreferences.getString(FIRST_NAME, "").orEmpty(),
                    lastName = sharedPreferences.getString(LAST_NAME, "").orEmpty(),
                    email = sharedPreferences.getString(EMAIL, "").orEmpty(),
                    phoneNumber = sharedPreferences.getString(MOBILE_NUMBER, "").orEmpty(),
                    locale = sharedPreferences.getString(LOCALE, "").orEmpty(),
                    organisations = getOrgsForUser())
            true
        }

    override fun saveUser(userInfo: UserInfo) {
        analytics.userInfo = userInfo
        sharedPreferences.edit()
                .putString(FIRST_NAME, userInfo.firstName)
                .putString(LAST_NAME, userInfo.lastName)
                .putString(EMAIL, userInfo.email)
                .putString(MOBILE_NUMBER, userInfo.phoneNumber)
                .putString(USER_ID, userInfo.userId)
                .putString(ORGANISATIONS, gson.toJson(userInfo.organisations))
                .putString(LOCALE, userInfo.locale)
                .apply()
    }

    override var savedPaymentInfo: SavedPaymentInfo?
        get() = returnSavedPaymentInfo()
        set(value) {
            value?.let { storeSavedPaymentInfo(value) }
        }

    override var paymentProvider: Provider?
        get() = returnSavedPaymentProvider()
        set(value) {
            value?.let {
                sharedPreferences.edit()
                        .putString(PROVIDER_ID, value.id)
                        .putString(PROVIDER_LOYALTY_PROGRAMMES, gson.toJson(value.loyalty))
                        .commit()
            }
        }

    @SuppressLint("ApplySharedPref")
    private fun storeSavedPaymentInfo(savedPaymentInfo: SavedPaymentInfo) {
        sharedPreferences.edit()
                .putString(LAST_FOUR, savedPaymentInfo.lastFour)
                .putString(CARD_TYPE, savedPaymentInfo.cardType?.value)
                .commit() //using apply will cause a bug

        userPaymentObservers.map {
            it?.onSavedPaymentInfoChanged(savedPaymentInfo) ?: run {
                userPaymentObservers.remove(it)
            }
        }
    }

    private fun returnSavedPaymentProvider(): Provider? {
        val id = sharedPreferences.getString(PROVIDER_ID, "").orEmpty()
        val loyaltyProgrammes = getLoyaltyProgrammesForUser()

        return if (id.isNotBlank()) Provider(id = id, loyalty = loyaltyProgrammes) else null
    }

    private fun getLoyaltyProgrammesForUser(): List<LoyaltyProgramme> {
        val listType = object : TypeToken<ArrayList<LoyaltyProgramme>>() {}.type
        return gson.fromJson(sharedPreferences.getString(PROVIDER_LOYALTY_PROGRAMMES, "[]"), listType)
    }

    private fun returnSavedPaymentInfo(): SavedPaymentInfo? {
        val lastFour = sharedPreferences.getString(LAST_FOUR, "").orEmpty()
        val cardType = sharedPreferences.getString(CARD_TYPE, "DEFAULT").orEmpty()

        return if (lastFour.isNotBlank() && cardType.isNotBlank()) {
            SavedPaymentInfo(lastFour, CardType.fromString(cardType))
        } else {
            null
        }
    }

    override fun deleteUser() {
        sharedPreferences.edit().putString(FIRST_NAME, null)
                .putString(LAST_NAME, null)
                .putString(EMAIL, null)
                .putString(MOBILE_NUMBER, null)
                .putString(USER_ID, null)
                .putString(LAST_FOUR, null)
                .putString(CARD_TYPE, null)
                .putString(PROVIDER_ID, null)
                .putString(PROVIDER_LOYALTY_PROGRAMMES, null)
                .apply()
    }

    private fun getOrgsForUser(): List<Organisation> {
        val listType = object : TypeToken<ArrayList<Organisation>>() {}.type
        return gson.fromJson(sharedPreferences.getString(ORGANISATIONS, "[]"), listType)
    }

    companion object {
        private const val FIRST_NAME = "first_name"
        private const val LAST_NAME = "last_name"
        private const val EMAIL = "email"
        private const val MOBILE_NUMBER = "mobile_number"
        private const val USER_ID = "user_id"
        private const val LOCALE = "locale"
        private const val ORGANISATIONS = "organisations"
        private const val LAST_FOUR = "last_four"
        private const val CARD_TYPE = "card_type"
        private const val PROVIDER_ID = "payment_provider_id"
        private const val PROVIDER_LOYALTY_PROGRAMMES = "payment_provider_loyalty_programmes"
        const val PREFERENCES_USER_NAME = "user"
    }

}
