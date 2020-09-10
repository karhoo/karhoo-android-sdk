package com.karhoo.sdk.api.network.adapter

fun void(): Void {
    val voidConstructor = Void::class.java.declaredConstructors[0]
    voidConstructor.isAccessible = true
    return voidConstructor.newInstance() as Void
}
