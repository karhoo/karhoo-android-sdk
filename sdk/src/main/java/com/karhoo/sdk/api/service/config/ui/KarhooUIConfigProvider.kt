package com.karhoo.sdk.api.service.config.ui

import com.karhoo.sdk.BuildConfig
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.UIConfig
import com.karhoo.sdk.api.model.UISetting
import com.karhoo.sdk.api.model.UISettings
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.karhoo.sdk.api.network.response.Resource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class KarhooUIConfigProvider : UIConfigProvider {

    override fun fetchConfig(uiConfigRequest: UIConfigRequest, organistionId: Organisation): Deferred<Resource<UIConfig>> {

        // TODO: Review before making publishing this project to open source

        val uiSettings = UISettings(
                mapOf(
                        BuildConfig.KARHOO_TRAVELLER_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false))),
                        BuildConfig.KARHOO_TRAVELLER_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false))),
                        BuildConfig.KARHOO_PROD_BRAINTREE_DEFAULT_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false))),
                        BuildConfig.KARHOO_SANDBOX_BRAINTREE_DEFAULT_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false))),
                        BuildConfig.KARHOO_SANDBOX_ADYEN_DEFAULT_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false))),
                        BuildConfig.KARHOO_STAGING_BRAINTREE_DEFAULT_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false))),
                        BuildConfig.KARHOO_STAGING_ADYEN_DEFAULT_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false))),
                        BuildConfig.KARHOO_US_ORGANISATION_ID to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(true),
                                        "addressMapOptions" to UIConfig(false)))))

        val uiConfig = uiSettings.uiSetting[organistionId.id]?.uiSetting?.get(uiConfigRequest.viewId)
        if (uiConfig != null) {
            return CompletableDeferred(Resource.Success(uiConfig))
        }
        return CompletableDeferred(Resource.Failure(KarhooError.NoConfigAvailableForView))
    }

}
