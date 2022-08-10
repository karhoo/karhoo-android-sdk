package com.karhoo.sdk.api.service.quotes

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.VehicleMappings
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.api.service.common.BaseCallInteractor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class VehicleMappingsInteractor @Inject constructor(
    credentialsManager: CredentialsManager,
    private val apiTemplate: APITemplate,
    context: CoroutineContext = Dispatchers.Main
) : BaseCallInteractor<VehicleMappings>(false, credentialsManager, apiTemplate, context) {

    override fun createRequest(): Deferred<Resource<VehicleMappings>> {
        return GlobalScope.async {
            return@async getVehicleMappings()
        }
    }

    private suspend fun getVehicleMappings(): Resource<VehicleMappings> {
        return when (val result = apiTemplate.vehicleMappings(KARHOO_IMAGE_RULES_CDN_URL).await()) {
            is Resource.Success -> Resource.Success(data = result.data)
            is Resource.Failure -> Resource.Failure(error = result.error)
        }
    }

    companion object {
        private const val KARHOO_IMAGE_RULES_CDN_URL =
            "https://cdn.karhoo.com/s/images/vehicles/config.json"
    }
}
