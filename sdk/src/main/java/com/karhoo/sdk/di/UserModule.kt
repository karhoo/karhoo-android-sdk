package com.karhoo.sdk.di

import android.content.Context
import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.api.KarhooApi
import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.credentials.KarhooCredentialsManager
import com.karhoo.sdk.api.datastore.user.KarhooUserManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.datastore.user.UserStore
import dagger.Module
import dagger.Provides

@Module
class UserModule(context: Context) {

    private val context = context.applicationContext
    private var userManager: UserManager? = null

    @Provides
    fun provideUserManager(analytics: Analytics): UserManager {

        var userManager = this.userManager

        if (userManager == null) {
            val sharedPreferences = context.getSharedPreferences(KarhooUserManager.PREFERENCES_USER_NAME, Context.MODE_PRIVATE)
            userManager = KarhooUserManager(sharedPreferences, analytics = analytics)
            this.userManager = userManager
            return userManager
        }
        return userManager
    }

    @Provides
    fun provideCredentialsManager(): CredentialsManager {
        val sharedPreferences = context.getSharedPreferences(KarhooCredentialsManager.PREFERENCES_CRED_NAME, Context.MODE_PRIVATE)
        return KarhooCredentialsManager(sharedPreferences)
    }

    @Provides
    fun provideUserStore(): UserStore {
        return KarhooApi.userStore
    }

}
