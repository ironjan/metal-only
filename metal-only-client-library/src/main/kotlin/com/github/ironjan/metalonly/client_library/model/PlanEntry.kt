package com.github.ironjan.metalonly.client_library.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.util.Date

data class PlanEntry(val start: Date,
                     val end: Date,
                     val showInformation: ShowInformation) {
    class Deserializer : ResponseDeserializable<PlanEntry> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, PlanEntry::class.java)!!
    }

    class ArrayDeserializer: ResponseDeserializable<Array<PlanEntry>> {
        override fun deserialize(reader: Reader): Array<PlanEntry>? {
            val type = object : TypeToken<Array<PlanEntry>>() {}.type
            return Gson().fromJson(reader, type)
        }
    }
}

