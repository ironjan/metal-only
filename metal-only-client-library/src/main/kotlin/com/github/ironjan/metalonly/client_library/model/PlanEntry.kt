package com.github.ironjan.metalonly.client_library.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.util.Date

data class PlanEntry(val start: Date,
                     val end: Date,
                     val showInformation: ShowInformation) {
    class Deserializer : ResponseDeserializable<PlanEntry> {

        override fun deserialize(reader: Reader): PlanEntry {
            return customDateFormatGson
                    .fromJson(reader, PlanEntry::class.java)!!
        }

    }


    class ArrayDeserializer : ResponseDeserializable<Array<PlanEntry>> {
        override fun deserialize(reader: Reader): Array<PlanEntry>? {
            val type = object : TypeToken<Array<PlanEntry>>() {}.type
            return customDateFormatGson.fromJson(reader, type)
        }
    }

    companion object {
        val customDateFormatGson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm")
                .create()
    }
}

