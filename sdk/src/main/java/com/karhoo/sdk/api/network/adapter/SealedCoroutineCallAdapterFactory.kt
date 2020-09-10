package com.karhoo.sdk.api.network.adapter

import com.karhoo.sdk.analytics.Analytics
import com.karhoo.sdk.analytics.Event
import com.karhoo.sdk.analytics.Payloader
import com.karhoo.sdk.api.KarhooError
import com.karhoo.sdk.api.network.response.Resource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.HttpURLConnection

class SealedCoroutineCallAdapterFactory private constructor(private val analyticsManager: Analytics) : CallAdapter.Factory() {

    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke(analyticsManager: Analytics) = SealedCoroutineCallAdapterFactory(analyticsManager)
    }

    override fun get(returnType: Type,
                     annotation: Array<out Annotation>,
                     retrofit: Retrofit): CallAdapter<*, *>? {
        checkIfReturnTypeIsValid(returnType)?.let { responseType ->
            return checkIfRawDeferredTypeIsResponse(responseType)
        } ?: run {
            return null
        }
    }

    private fun checkIfReturnTypeIsValid(returnType: Type): Type? {
        if (Deferred::class.java != getRawType(returnType)) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                    "Deferred return type must be parameterized as Deferred<Foo> or Deferred<out Foo>")
        }
        return getParameterUpperBound(0, returnType)
    }

    private fun checkIfRawDeferredTypeIsResponse(responseType: Type): CallAdapter<*, *>? {
        return if (getRawType(responseType) == Response::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException(
                        "Response must be parameterized as Response<Foo> or Response<out Foo>")
            }
            ResponseCallAdapter<Any>(getParameterUpperBound(0, responseType), analyticsManager)
        } else {
            BodyCallAdapter<Any>(responseType, analyticsManager)
        }
    }

    private class ResponseCallAdapter<T>(private val responseType: Type, private val analyticsManager: Analytics) : CallAdapter<T, Deferred<Resource<T>>> {

        override fun responseType(): Type {
            if (responseType is ParameterizedType) {
                val nonResourceType = responseType.actualTypeArguments[0]
                if (nonResourceType !is Resource<*>) {
                    return nonResourceType
                }
            }
            return responseType
        }

        override fun adapt(call: Call<T>): Deferred<Resource<T>> {
            val deferred = CompletableDeferred<Resource<T>>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.complete(Resource.Failure(error = KarhooError.fromThrowable(t)))
                    val url = call.request().url().toString()
                    analyticsManager.fireEvent(Event.REQUEST_ERROR, Payloader.Builder.builder.requestError(t.localizedMessage, url).build())
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            deferred.complete(Resource.Success(it))
                        } ?: run {
                            if (response.code() == HttpURLConnection.HTTP_NO_CONTENT
                                    || response.code() == HttpURLConnection.HTTP_CREATED) {
                                deferred.complete(Resource.Success(void() as T))
                            }
                        }
                    } else {
                        deferred.complete(Resource.Failure(error = KarhooError.fromThrowable(HttpException(response))))
                    }
                }
            })
            return deferred
        }
    }

    private class BodyCallAdapter<T>(private val responseType: Type, private val analyticsManager: Analytics) : CallAdapter<T, Deferred<Resource<T>>> {

        override fun responseType(): Type {
            if (responseType is ParameterizedType) {
                val nonResourceType = responseType.actualTypeArguments[0]
                if (nonResourceType !is Resource<*>) {
                    return nonResourceType
                }
            }
            return responseType
        }

        override fun adapt(call: Call<T>): Deferred<Resource<T>> {
            val deferred = CompletableDeferred<Resource<T>>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.complete(Resource.Failure(error = KarhooError.fromThrowable(t)))
                    val url = call.request().url().toString()
                    analyticsManager.fireEvent(Event.REQUEST_ERROR, Payloader.Builder.builder.requestError(t.localizedMessage, url).build())
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            deferred.complete(Resource.Success(it))
                        } ?: run {
                            if (response.code() == HttpURLConnection.HTTP_NO_CONTENT
                                    || response.code() == HttpURLConnection.HTTP_CREATED) {
                                deferred.complete(Resource.Success(void() as T))
                            }
                        }
                    } else {
                        deferred.complete(Resource.Failure(error = KarhooError.fromThrowable(HttpException(response))))
                    }
                }
            })

            return deferred
        }
    }
}
