package com.karhoo.sdk.call

import com.karhoo.sdk.api.network.observable.Observable

interface PollCall<T> : Call<T> {

    fun observable(): Observable<T>

}
