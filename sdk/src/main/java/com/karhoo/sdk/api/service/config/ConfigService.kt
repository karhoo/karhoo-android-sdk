package com.karhoo.sdk.api.service.config

import com.karhoo.sdk.api.model.FeatureFlags
import com.karhoo.sdk.api.model.UIConfig
import com.karhoo.sdk.api.network.request.FeatureFlagsRequest
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.karhoo.sdk.call.Call

interface ConfigService {

    fun uiConfig(uiConfigRequest: UIConfigRequest): Call<UIConfig>

    fun featureFlags(featureFlagsRequest: FeatureFlagsRequest): Call<FeatureFlags>
}
