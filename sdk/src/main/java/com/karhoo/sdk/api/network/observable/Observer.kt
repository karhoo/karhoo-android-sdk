package com.karhoo.sdk.api.network.observable

interface Observer<T> {

    fun onValueChanged(value: T)

}
