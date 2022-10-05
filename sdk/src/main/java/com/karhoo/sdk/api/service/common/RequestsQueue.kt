package com.karhoo.sdk.api.service.common

import com.karhoo.sdk.BuildConfig
import com.karhoo.sdk.api.network.observable.Observable.Companion.BASE_POLL_TIME
import com.karhoo.sdk.api.network.response.Resource
import kotlinx.coroutines.delay
import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

internal object RequestsQueue {
    private var queue: ConcurrentLinkedQueue<DelayedRequest<Any>> = ConcurrentLinkedQueue()
    private var processing: AtomicBoolean = AtomicBoolean(false)
    private const val TAG = "RequestsQueue"

    class DelayedRequest<RESPONSE> {
        var subscriber: (Resource<RESPONSE>) -> Unit
        var baseInteractor: BaseCallInteractor<RESPONSE>? = null
        var pollInteractor: BasePollCallInteractor<RESPONSE>? = null

        constructor(
            subscriber: (Resource<RESPONSE>) -> Unit,
            interactor: BaseCallInteractor<RESPONSE>
        ) {
            this.subscriber = subscriber
            this.baseInteractor = interactor
        }

        constructor(
            subscriber: (Resource<RESPONSE>) -> Unit,
            interactor: BasePollCallInteractor<RESPONSE>
        ) {
            this.subscriber = subscriber
            this.pollInteractor = interactor
        }

    }

    fun addRequest(request: DelayedRequest<Any>) {
        log("Adding request to queue")

        queue.add(request)
    }

    suspend fun consumeRequests() {
        if (!processing.get()) {
            log("Starting to consume requests from the queue - queue size " + queue.size)

            processing.set(true)

            while (queue.peek() != null) {
                val delayedRequest = queue.poll()

                log("Consuming request from queue")

                delayedRequest?.baseInteractor?.let {
                    delayedRequest.subscriber(it.createRequest().await())
                }
                delayedRequest?.pollInteractor?.let {
                    delay(BASE_POLL_TIME)
                    delayedRequest.subscriber(it.createRequest().await())
                }
            }

            log("All requests from the queue have been consumed")

            processing.set(false)
        }
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }
}
