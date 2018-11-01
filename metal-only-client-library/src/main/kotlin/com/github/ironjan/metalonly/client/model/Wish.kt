package com.github.ironjan.metalonly.client.model

import com.google.gson.Gson

data class Wish(val nick: String,
                val artist: String,
                val title: String,
                val greeting : String) {

    class Serializer {

        fun serialize(wish: Wish): String {
            return Gson().toJson(wish)!!
        }

    }


}