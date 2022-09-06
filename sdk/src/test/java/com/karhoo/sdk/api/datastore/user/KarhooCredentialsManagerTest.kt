package com.karhoo.sdk.api.datastore.user

import android.content.SharedPreferences
import com.karhoo.sdk.api.datastore.credentials.KarhooCredentialsManager
import com.karhoo.sdk.api.model.Credentials
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class KarhooCredentialsManagerTest {

    internal var credentials: Credentials = mock()
    internal var preferences: SharedPreferences = mock()
    internal var editor: SharedPreferences.Editor = mock()

    internal lateinit var karhooCredentialsManager: KarhooCredentialsManager

    @Before
    fun setUp() {
        karhooCredentialsManager = KarhooCredentialsManager(preferences)
    }

    /**
     * Given    A user has credentials
     * When     Saving credentials to the preferences
     * Then     The credentials should be saved for future use
     *
     */
    @Test
    fun `saving user credentials to shared preferences`() {
        whenever(credentials.accessToken).thenReturn("")
        whenever(credentials.expiresIn).thenReturn(1L)
        whenever(credentials.refreshToken).thenReturn("")

        whenever(preferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), anyString())).thenReturn(editor)
        whenever(editor.putLong(anyString(), anyLong())).thenReturn(editor)
        doNothing().whenever(editor).apply()

        karhooCredentialsManager.saveCredentials(credentials)

        verify(editor, atLeastOnce()).putString(anyString(), anyString())
        verify(editor, atLeastOnce()).putLong(anyString(), anyLong())
    }

    /**
     * Given    A users credentials have been saved to shared preferences
     * When     Trying to retrieve the saved credentials
     * Then     The user should be returned from shared preferences
     *
     */
    @Test
    fun `getting credentials from shared preferences returns credentials`() {
        whenever(preferences.getString(anyString(), anyString())).thenReturn("")
        whenever(preferences.getLong(anyString(), anyLong())).thenReturn(1L)

        val credentials = karhooCredentialsManager.credentials

        assertEquals("", credentials.accessToken)
        assertEquals("", credentials.refreshToken)
        assertEquals(java.lang.Long.valueOf(1L), credentials.expiresIn)
        assertEquals(java.lang.Long.valueOf(1L), credentials.refreshExpiresIn)
    }

    /**
     * Given    A user has requested that they want there details removed
     * When     Trying to delete the credentials
     * Then     The credentials should be deleted from shared preferences
     *
     */
    @Test
    fun `delete credentials from shared prefences nulls out the values`() {
        whenever(preferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), eq(null))).thenReturn(editor)
        whenever(editor.putLong(anyString(), anyLong())).thenReturn(editor)
        doNothing().whenever(editor).apply()

        karhooCredentialsManager.deleteCredentials()

        verify(editor, atLeastOnce()).putString(anyString(), eq(null))
        verify(editor, atLeastOnce()).putLong(anyString(), anyLong())
        verify(editor).apply()
    }

    /**
     * Given    A request is made to see if the JWT is valid
     * When     The JWT is valid
     * Then     A boolean of true should be returned
     *
     */
    @Test
    fun `valid token returns true`() {
        whenever(preferences.getLong(EXPIRES_IN, 0L)).thenReturn(System.currentTimeMillis() + 1000)
        whenever(preferences.getLong(REFRESH_EXPIRES_IN, 0L)).thenReturn(System.currentTimeMillis() + 1000)

        assertTrue(karhooCredentialsManager.isValidToken)
        assertTrue(karhooCredentialsManager.isValidRefreshToken)
    }

    /**
     * Given    A request is made to see if the JWT is valid
     * When     The JWT is valid
     * Then     A boolean of true should be returned
     *
     */
    @Test
    fun `invalid token returns false`() {
        whenever(preferences.getLong(EXPIRES_IN, 0L)).thenReturn(0L)
        whenever(preferences.getLong(REFRESH_EXPIRES_IN, 0L)).thenReturn(0L)

        assertFalse(karhooCredentialsManager.isValidToken)
        assertFalse(karhooCredentialsManager.isValidRefreshToken)
    }

    companion object {
        private const val EXPIRES_IN = "expires_in"
        private const val REFRESH_EXPIRES_IN = "refresh_expires_in"
    }
}
