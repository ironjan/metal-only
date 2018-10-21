package com.github.ironjan.metalonly.client.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

data class Track(val artist: String,
                 val title: String) {
    class Deserializer : ResponseDeserializable<Track> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Track::class.java)!!
    }
}
