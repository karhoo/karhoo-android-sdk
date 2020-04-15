package com.karhoo.sdk.api.service.config.ui

import com.karhoo.sdk.api.model.Organisation
import com.karhoo.sdk.api.model.UIConfig
import com.karhoo.sdk.api.network.request.UIConfigRequest
import com.karhoo.sdk.api.network.response.Resource
import kotlinx.coroutines.Deferred

interface UIConfigProvider {

    fun fetchConfig(uiConfigRequest: UIConfigRequest, organistionId: Organisation): Deferred<Resource<UIConfig>>

}