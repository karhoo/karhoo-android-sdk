package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.datastore.credentials.CredentialsManager
import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.Places
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.client.APITemplate
import com.karhoo.sdk.api.network.request.LocationInfoRequest
import com.karhoo.sdk.api.network.request.PlaceSearch
import com.karhoo.sdk.call.Call
import javax.inject.Inject

class KarhooAddressService : AddressService {

    @Inject
    internal lateinit var credentialsManager: CredentialsManager

    @Inject
    internal lateinit var apiTemplate: APITemplate

    override fun placeSearch(placeSearch: PlaceSearch): Call<Places> = PlacesInteractor(credentialsManager, apiTemplate).apply {
        this.placeSearch = placeSearch
    }

    override fun reverseGeocode(position: Position): Call<LocationInfo> = ReverseGeolocateInteractor(credentialsManager, apiTemplate).apply {
        this.position = position
    }

    override fun locationInfo(locationInfoRequest: LocationInfoRequest): Call<LocationInfo> = LocationInfoInteractor(credentialsManager, apiTemplate).apply {
        this.locationInfoRequest = locationInfoRequest
    }

}
