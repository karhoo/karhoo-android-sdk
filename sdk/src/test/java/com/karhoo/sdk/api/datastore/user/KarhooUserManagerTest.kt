package com.karhoo.sdk.api.datastore.user

import android.content.SharedPreferences
import com.google.gson.Gson
import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.model.CardType
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.UserInfo
import com.karhoo.sdk.api.service.common.InteractorConstants
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooUserManagerTest {

    internal var preferences: SharedPreferences = mock()
    internal var editor: SharedPreferences.Editor = mock()
    internal var analytics: Analytics = mock()

    internal lateinit var karhooUserManager: KarhooUserManager

    private val userInfo: UserInfo
        get() = UserInfo(
                firstName = "John",
                lastName = "Smith",
                email = "name@email.com",
                phoneNumber = "1234567",
                userId = "123",
                locale = "",
                organisations = organisations)

    private val organisations = mutableListOf(Organisation(
            id = "123ABC",
            name = "Karhoo",
            roles = mutableListOf(InteractorConstants.REQUIRED_ROLE)
                                                          ))

    @Before
    fun setUp() {
        karhooUserManager = KarhooUserManager(preferences, analytics)
    }

    /**
     * Given    A user has been created
     * When     Saving a user to the preferences
     * Then     The user should be saved for future use
     *
     */
    @Test
    fun `saving user to shared preferences`() {
        whenever(preferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), anyString())).thenReturn(editor)
        doNothing().whenever(editor).apply()

        karhooUserManager.saveUser(userInfo)

        verify(editor, atLeastOnce()).putString(anyString(), anyString())
    }

    /**
     * Given    A user has been saved to shared preferences
     * When     Trying to retrieve the saved user
     * Then     The user should be returned from shared preferences
     *
     */
    @Test
    fun `getting user from shared preferences returns user details`() {
        whenever(preferences.getString(anyString(), anyString())).thenReturn("TEST")
        whenever(preferences.getString("organisations", "[]")).thenReturn(Gson().toJson(organisations))

        val user = karhooUserManager.user

        assertEquals("TEST", user.userId)
        assertEquals("TEST", user.email)
        assertEquals("TEST", user.firstName)
        assertEquals("TEST", user.lastName)
        assertEquals("TEST", user.phoneNumber)
    }

    /**
     * Given    A user has requested that they want their details removed
     * When     Trying to delete their details
     * Then     The details should be deleted from shared preferences
     *
     */
    @Test
    fun `delete user from shared preferences nulls out the values`() {
        whenever(preferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), eq(null))).thenReturn(editor)

        karhooUserManager.deleteUser()

        verify(editor).apply()
    }

    /**
     * Given    A user has requested that they want there saved payment info removed
     * Then     The the saved payment info should be deleted from shared preferences
     *
     */
    @Test
    fun `delete saved payment info from shared preferences nulls out the values`() {
        whenever(preferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), eq(null))).thenReturn(editor)

        karhooUserManager.deleteSavedPaymentInfo()

        verify(editor).apply()
    }

    /**
     * Given    The payment info is set
     * When     We try to clear/delete it
     * Then     The the saved payment info should be deleted from shared preferences and be set
     * to null in the user manager
     *
     */
    @Test
    fun `When set the some payment info and then delete it, the saved payment info is null`() {
        whenever(preferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), eq(null))).thenReturn(editor)
        whenever(editor.putString(anyString(), anyString())).thenReturn(editor)

        karhooUserManager.savedPaymentInfo = SavedPaymentInfo("0000", CardType.MASTERCARD)
        karhooUserManager.deleteSavedPaymentInfo()

        verify(editor).commit()

        assertNull(karhooUserManager.savedPaymentInfo)
    }

    /**
     * Given    The payment info is not set
     * When     We still try to clear/delete it
     * Then     The the saved payment info should be deleted from shared preferences and be set
     * to null in the user manager
     *
     */
    @Test
    fun `When the payment info is null still try to delete it, the saved payment info remains null`() {
        whenever(preferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), eq(null))).thenReturn(editor)

        karhooUserManager.deleteSavedPaymentInfo()

        verify(editor).apply()

        assertNull(karhooUserManager.savedPaymentInfo)
    }

    /**
     * Given    A valid user has been saved to the store
     * When     Wanting to know if a user if the user has valid credentials
     * Then     A boolean should be returned to say if the user is valid
     */
    @Test
    fun `checking if a user is valid returns a boolean`() {
        whenever(preferences.getString(anyString(), anyString())).thenReturn("VALID")
        whenever(preferences.getString("organisations", "[]")).thenReturn(Gson().toJson(organisations))

        assertTrue(karhooUserManager.isUserStillValid)
    }

    /**
     * Given    A no user has been saved to the store
     * When     Wanting to know if a user if the user has valid credentials
     * Then     A boolean should be returned to say if the user is valid
     */
    @Test
    fun `checking if a user is not valid returns a boolean`() {
        whenever(preferences.getString(anyString(), anyString())).thenReturn("")

        assertFalse(karhooUserManager.isUserStillValid)
    }
}
