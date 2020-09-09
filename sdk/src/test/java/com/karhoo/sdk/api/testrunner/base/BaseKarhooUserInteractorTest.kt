package com.karhoo.sdk.api.testrunner.base

import android.content.Context
import com.karhoo.sdk.api.KarhooSDKConfigurationProvider
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.AuthenticationMethod
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.testrunner.UnitTestSDKConfig
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
open class BaseKarhooUserInteractorTest {

    val applicationContext: Context = mock()
    val apiTemplate: APITemplate = mock()
    val context: CoroutineContext = Dispatchers.Unconfined
    val credentialsManager: CredentialsManager = mock()

    @Before
    open fun setUp() {
        KarhooSDKConfigurationProvider.setConfig(configuration = UnitTestSDKConfig(context =
                                                                                   applicationContext,
                                                                                   authenticationMethod = AuthenticationMethod.KarhooUser()))
    }

    @Test
    fun emptyTest() {
        assert(true)
    }
}