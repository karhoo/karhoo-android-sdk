package com.karhoo.sdk.api.service.fare

import com.karhoo.sdk.api.model.Fare
import com.karhoo.sdk.call.Call

interface FareService {

    fun fareDetails(tripId: String): Call<Fare>

}