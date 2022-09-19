package com.karhoo.sdk.api.service.common

import com.karhoo.sdk.api.network.response.Resource
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

internal object RequestsQueue {
    private var queue: Queue<DelayedRequest<Any>> = LinkedList()
    private var processing: AtomicBoolean = AtomicBoolean(false)

    class DelayedRequest<RESPONSE> {
        var subscriber: (Resource<RESPONSE>) -> Unit
        var baseInteractor: BaseCallInteractor<RESPONSE>? = null
        var pollInteractor: BasePollCallInteractor<RESPONSE>? = null

        constructor(subscriber: (Resource<RESPONSE>) -> Unit, interactor: BaseCallInteractor<RESPONSE>) {
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
        queue.add(request)
    }

    suspend fun consumeRequests() {
        if(!processing.get()) {
            processing.set(true)

            queue.map { delayedRequest ->

                delayedRequest.baseInteractor?.let {
                    delayedRequest.subscriber(it.createRequest().await())
                }
                delayedRequest.pollInteractor?.let {
                    delayedRequest.subscriber(it.createRequest().await())
                }
            }

            queue.clear()
        }
    }
}
