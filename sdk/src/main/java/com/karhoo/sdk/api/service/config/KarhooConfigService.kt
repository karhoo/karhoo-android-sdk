package com.karhoo.sdk.api.service.config

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.datastore.user.UserManager
import com.karhoo.sdk.api.model.FeatureFlags
import com.karhoo.sdk.api.model.UIConfig
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.FeatureFlagsRequest
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.karhoo.sdk.api.service.config.ui.KarhooUIConfigProvider
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooConfigService : ConfigService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    @Inject
    internal lateinit var userManager: UserManager

    private val uiConfigProvider = KarhooUIConfigProvider()

    override fun uiConfig(uiConfigRequest: UIConfigRequest): Call<UIConfig> = UIConfigInteractor(
        credentialsManager = credentialsManager,
        apiTemplate = apiTemplate,
        uiConfigProvider = uiConfigProvider,
        userManager = userManager
    ).apply {
        this.uiConfigRequest = uiConfigRequest
    }

    override fun featureFlags(featureFlagsRequest: FeatureFlagsRequest): Call<FeatureFlags> =
        FeatureFlagsInteractor(
            credentialsManager = credentialsManager,
            apiTemplate = apiTemplate
        ).apply {
            this.featureFlagsRequest = featureFlagsRequest
        }
}
