package com.karhoo.sdk.api.service.drivertracking

import com.karhoo.sdk.api.model.DriverTrackingInfo
import com.karhoo.sdk.call.PollCall

interface DriverTrackingService {

    fun trackDriver(tripId: String): PollCall<DriverTrackingInfo>

}
