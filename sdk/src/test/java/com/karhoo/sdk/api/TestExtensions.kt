package com.karhoo.sdk.api

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import org.mockito.ArgumentCaptor

inline fun <reified A: Any,reified B: Any> pairCaptor(): Pair<KArgumentCaptor<A>, KArgumentCaptor<B>> {
    return Pair(KArgumentCaptor(ArgumentCaptor.forClass(A::class.java), A::class),
            KArgumentCaptor(ArgumentCaptor.forClass(B::class.java), B::class))
}
