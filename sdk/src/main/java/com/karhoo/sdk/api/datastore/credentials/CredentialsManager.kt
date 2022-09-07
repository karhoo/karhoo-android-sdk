package com.karhoo.sdk.api.datastore.credentials

import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.model.Credentials
import com.karhoo.sdk.api.network.client.APITemplate

interface CredentialsManager {

    val credentials: Credentials

    val isValidToken: Boolean

    val isValidRefreshToken: Boolean

    fun saveCredentials(credentials: Credentials, apiTemplate: APITemplate? = null, config: AuthenticationMethod? =null)

    fun deleteCredentials()

}
