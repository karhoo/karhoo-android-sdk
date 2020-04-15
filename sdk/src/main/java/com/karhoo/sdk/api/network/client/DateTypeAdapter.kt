package com.karhoo.sdk.api.network.client

import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DateTypeAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm").apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            formatter.parse(json.asString)
        } catch (e: Exception) {
            Date()
        }
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        val string = src?.let {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm").apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            formatter.format(src)
        }.orEmpty()
        return context.serialize(string)
    }

}