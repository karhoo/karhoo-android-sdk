package com.karhoo.sdk.api.network.observable

import com.karhoo.sdk.api.network.response.Resource
import com.karhoo.sdk.call.Call
import java.lang.ref.WeakReference
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

internal class KarhooObservable<T>(val call: Call<T>) : Observable<T> {

    override var count: Int = 0
        get() = subscribers.size

    private var subscribers = mutableListOf<WeakReference<Observer<Resource<T>>>>()

    private var timer: Timer? = null

    override fun subscribe(observer: Observer<Resource<T>>, repeatInterval: Long) {
        subscribers.add(WeakReference(observer))
        if (count == 1) {
            beginPolling(repeatInterval)
        }
    }

    private fun beginPolling(repeatInterval: Long) {
        timer = Timer()
        timer?.scheduleAtFixedRate(0, repeatInterval) {
            call.execute { value -> notifyAllObservers(value) }
        }
    }

    private fun notifyAllObservers(value: Resource<T>) {
        subscribers.forEach {
            it.get()?.let {
                it.onValueChanged(value = value)
            } ?: run {
                subscribers.remove(it)
            }
        }
    }

    override fun unsubscribe(observer: Observer<Resource<T>>) {
        removeObserver(observer)
        cancelPolling()
    }

    private fun cancelPolling() {
        if (subscribers.size < 1) {
            timer?.apply {
                cancel()
                purge()
            }
        }
    }

    private fun removeObserver(observer: Observer<Resource<T>>) {
        val it = subscribers.iterator()
        while (it.hasNext()) {
            if (it.next().get() == observer) {
                it.remove()
            }
        }
    }

}
