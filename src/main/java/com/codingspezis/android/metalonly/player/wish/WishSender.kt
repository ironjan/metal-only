package com.codingspezis.android.metalonly.player.wish

import android.text.TextUtils

import com.codingspezis.android.metalonly.player.BuildConfig
import com.codingspezis.android.metalonly.player.utils.UrlConstants

import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HTTP
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.io.IOException
import java.util.ArrayList

/**
 * Restored class since AA REST api doesn't work for wishes.
 */
class WishSender {
    private val SENDER_LOGGER = LoggerFactory.getLogger(WishSender::class.java.simpleName)

    private val nick: String
    private val artist: String?
    private val title: String?
    private val greet: String

    private val callback: Callback

    constructor(callback: Callback, nick: String, greet: String, artist: String? = null, title: String? = null) {
        this.nick = nick
        this.artist = artist
        this.title = title
        this.greet = greet
        this.callback = callback
    }


    fun send() {
        val client = DefaultHttpClient()
        val post = HttpPost(UrlConstants.METAL_ONLY_WUNSCHSCRIPT_POST_URL)
        val pairs = ArrayList<NameValuePair>(4)

        if (!TextUtils.isEmpty(nick)) {
            pairs.add(BasicNameValuePair(KEY_NICK, nick))
        }
        if (!TextUtils.isEmpty(artist)) {
            pairs.add(BasicNameValuePair(KEY_ARTIST, artist))
        }
        if (!TextUtils.isEmpty(title)) {
            pairs.add(BasicNameValuePair(KEY_SONG, title))
        }
        if (!TextUtils.isEmpty(greet)) {
            pairs.add(BasicNameValuePair(KEY_GREET, greet))
        }

        try {
            val entity = UrlEncodedFormEntity(pairs, "UTF-8")
            entity.setContentEncoding(HTTP.UTF_8)
            post.entity = entity
            val response = client.execute(post)
            if (response.statusLine.toString() == "HTTP/1.1 200 OK") {
                // TODO handle error cases by parsing HTML
                callback.onSuccess()
            } else {
                callback.onFail()
            }
        } catch (e: IOException) {
            callback.onException(e)
        }

        if (BuildConfig.DEBUG) SENDER_LOGGER.debug("run() done")
    }


    interface Callback {
        fun onSuccess()
        fun onFail()
        fun onException(e: Exception)
    }

    companion object {

        private val KEY_NICK = "nick"
        private val KEY_ARTIST = "artist"
        private val KEY_SONG = "song"
        private val KEY_GREET = "greet"
    }
}