package com.github.ironjan.metalonly.client_library.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

data class PlanEntry(val day: String,
                     val time: String,
                     val duration: Int,
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

