package com.github.ironjan.metalonly.client.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

data class ShowInformation(val moderator: String,
                           val show: String,
                           val genre: String) {
    class Deserializer : ResponseDeserializable<ShowInformation> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, ShowInformation::class.java)!!
    }
}
