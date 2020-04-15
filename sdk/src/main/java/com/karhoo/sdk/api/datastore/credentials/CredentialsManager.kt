package com.karhoo.sdk.api.datastore.credentials

import com.karhoo.sdk.api.model.Credentials

interface CredentialsManager {

    val credentials: Credentials

    val isValidToken: Boolean

    val isValidRefreshToken: Boolean

    fun saveCredentials(credentials: Credentials)

    fun deleteCredentials()

}
