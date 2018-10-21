package com.github.ironjan.metalonly.client_library.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Reader

data class Stats(val showInformation: ShowInformation,
                 val maxNoOfWishesReached: Boolean,
                 val maxNoOfGreetingsReached: Boolean,
                 val maxNoOfWishes: Int,
                 val maxNoOfGreetings: Int,
                 val track: Track) {
    class Deserializer : ResponseDeserializable<Stats> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, Stats::class.java)!!
    }
}
