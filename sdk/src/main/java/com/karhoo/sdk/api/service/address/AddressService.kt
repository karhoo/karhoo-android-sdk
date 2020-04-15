package com.karhoo.sdk.api.service.address

import com.karhoo.sdk.api.model.LocationInfo
import com.karhoo.sdk.api.model.Places
import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.network.request.LocationInfoRequest
import com.karhoo.sdk.api.network.request.PlaceSearch
import com.karhoo.sdk.call.Call

interface AddressService {

    fun placeSearch(placeSearch: PlaceSearch): Call<Places>

    fun reverseGeocode(position: Position): Call<LocationInfo>

    fun locationInfo(locationInfoRequest: LocationInfoRequest): Call<LocationInfo>

}
