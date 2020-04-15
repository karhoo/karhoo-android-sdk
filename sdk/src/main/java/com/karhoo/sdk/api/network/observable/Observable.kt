package com.karhoo.sdk.api.network.observable

import com.karhoo.sdk.api.network.response.Resource

interface Observable<T> {

    var count: Int

    fun subscribe(observer: Observer<Resource<T>>, repeatInterval: Long = BASE_POLL_TIME)

    fun unsubscribe(observer: Observer<Resource<T>>)

    companion object {
        const val BASE_POLL_TIME = 5000L
    }

}