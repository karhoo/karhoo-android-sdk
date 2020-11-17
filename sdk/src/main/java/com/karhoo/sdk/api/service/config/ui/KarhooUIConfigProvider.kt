package com.karhoo.sdk.api.service.config.ui

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

        //defaultForKarhooUsers : a1013897-132a-456c-9be2-636979095ad9
        //portalUser: 089a666b-a6ce-4e75-8d7f-12d8f0208f1b
        //Mon Chauffer: 5a54722d-e699-4da6-801f-a5652e6e31f7

        // TODO: Review before making publishing this project to open source

        val uiSettings = UISettings(
                mapOf(
                        "ed5ae432-1ff7-4d69-a4a6-2d4a65c81b0c" to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(false),
                                        "addressMapOptions" to UIConfig(false))),
                        "a1013897-132a-456c-9be2-6979095ad9" to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(false),
                                        "addressMapOptions" to UIConfig(false))),
                        "5fc4f33b-2832-466e-9943-8728589ef727" to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(false),
                                        "addressMapOptions" to UIConfig(false))),
                        "23661866-6554-46bf-977e-21430a3e1f22" to UISetting(
                                mapOf(
                                        "additionalFeedbackView" to UIConfig(false),
                                        "addressMapOptions" to UIConfig(false))),
                        "5a54722d-e699-4da6-801f-a5652e6e31f7" to UISetting(
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
