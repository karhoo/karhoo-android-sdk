package com.karhoo.sdk.di

import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.analytics.AnalyticsManager
import dagger.Module
import dagger.Provides

@Module
class AnalyticsModule() {

    @Provides
    fun provideAnalytics(): Analytics = AnalyticsManager

}