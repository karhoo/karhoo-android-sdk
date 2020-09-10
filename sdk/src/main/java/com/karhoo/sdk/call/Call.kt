package com.karhoo.sdk.call

import com.karhoo.sdk.api.network.response.Resource

interface Call<T> {

    fun execute(subscriber: (Resource<T>) -> Unit)

}
