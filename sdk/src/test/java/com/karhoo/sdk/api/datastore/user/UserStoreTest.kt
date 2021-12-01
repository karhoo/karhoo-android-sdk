package com.karhoo.sdk.api.datastore.user

import com.karhoo.sdk.analytics.AnalyticsManager.userInfo
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.CardType
import com.karhoo.sdk.api.model.LoyaltyStatus
import com.karhoo.sdk.api.model.UserInfo
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserStoreTest {

    private var userManager: UserManager = mock()

    // Needed for InjectMocks
    private var credentialsManager: CredentialsManager = mock()

    @InjectMocks
    private lateinit var userStore: KarhooUserStore

    /**
     * Given:   No User is logged in
     * When:    Asked to get current user
     * Then:    an empty userdetails should be returned to the caller
     */
    @Test
    fun `no logged in user should return empty user`() {
        userStore.currentUser
        verify(userManager, atLeastOnce()).user
    }


    /**
     * Given:   No user is set
     * When:    User is passed to be set
     * Then:    The stored user is the passed user
     */
    @Test
    fun `new logged in user is replaces current user`() {
        val userInfo1: UserInfo = mock()
        val userInfo2: UserInfo = mock()

        whenever(userManager.user).thenReturn(userInfo1)
        Assert.assertEquals(userInfo1, userStore.currentUser)
        whenever(userManager.user).thenReturn(userInfo2)
        Assert.assertEquals(userInfo2, userStore.currentUser)
    }

    /**
     * Given:   User is set
     * When:    Removed is called to reset the user
     * Then:    The users value should be null
     */
    @Test
    fun `removing set user returns the saved user to null`() {
        doNothing().whenever(userManager).deleteUser()
        val userInfo = createUser("", "", "", "")
        whenever(userManager.user).thenReturn(userInfo)
        val user = userStore.currentUser
        userStore.removeCurrentUser()
        assertEquals(user, userInfo)
    }

    /**
     * Given:   SavedPaymentInfo is set
     * When:    Delete is called to reset the payment info
     * Then:    The payment info should be null
     */
    @Test
    fun `removing set payment info returns the saved payment info to null`() {
        doNothing().whenever(userManager).deleteSavedPaymentInfo()

        userStore.clearSavedPaymentInfo()

        verify(userManager).deleteSavedPaymentInfo()
    }

    /**
     * Given:   A valid user is saved
     * When:    Checking if the saved user is valid
     * Then:    A boolean of true should be returned
     *
     */
    @Test
    fun `validating a user who is valid`() {
        whenever(userManager.isUserStillValid).thenReturn(true)
        Assert.assertTrue(userStore.isCurrentUserValid)
    }

    /**
     * Given:   An invalid user is saved
     * When:    Checking if the saved user is valid
     * Then:    A boolean of false should be returned
     *
     */
    @Test
    fun `validating a user who is not valid`() {
        whenever(userManager.isUserStillValid).thenReturn(false)
        Assert.assertFalse(userStore.isCurrentUserValid)
    }

    @Test
    fun `When adding a loyalty program in the user manager, then the user store returns itcorrectly`() {
        whenever(userManager.loyaltyStatus).thenReturn(LoyaltyStatus(10, canBurn = false,
                                                                     canEarn = false))
        assertEquals(userStore.loyaltyStatus?.canBurn, false)
        assertEquals(userStore.loyaltyStatus?.canEarn, false)
        assertEquals(userStore.loyaltyStatus?.points, 10)
    }

    @Test
    fun `When adding a null in the user manager, then the user store returns it correctly`() {
        whenever(userManager.loyaltyStatus).thenReturn(null)
        assertNull(userStore.loyaltyStatus)
    }

    private fun createUser(firstname: String, lastname: String, email: String, phone: String): UserInfo {
        return UserInfo(
                firstName = firstname,
                lastName = lastname,
                email = email,
                phoneNumber = phone,
                countryCode = "",
                avatarUrl = "",
                locale = "en-GB",
                organisations = mutableListOf())
    }

}
