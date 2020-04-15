package com.karhoo.sdk.analytics

import com.karhoo.sdk.api.model.Position
import com.karhoo.sdk.api.model.UserInfo

interface Analytics {

    fun initialise()

    fun fireEvent(event: Event, payload: Payloader)

    fun fireEvent(event: Event)

    var userInfo: UserInfo?

    var usersLatLng: Position?

    var deviceId: String

    var sessionId: String

}
