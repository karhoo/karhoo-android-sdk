package com.karhoo.sdk.api.network.response

import com.karhoo.sdk.api.KarhooError

sealed class Resource<out T> {

    data class Success<out T>(val data: T, val correlationId: String? = null) : Resource<T>()

    data class Failure<out T>(val error: KarhooError, val correlationId: String? = null) : Resource<T>()

}
