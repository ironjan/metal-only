package com.github.ironjan.metalonly.client_library

import android.text.TextUtils
import com.hypertrack.hyperlog.HyperLog
import com.squareup.okhttp.MultipartBuilder
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import java.io.IOException

/**
 * Restored class since AA REST api doesn't work for wishes.
 */
class WishSender {
    private val nick: String
    private val artist: String?
    private val title: String?
    private val greet: String

    private val callback: Callback

    private val TAG: String = "WishSender"

    constructor(callback: Callback, nick: String, greet: String, artist: String? = null, title: String? = null) {
        this.nick = nick
        this.artist = artist
        this.title = title
        this.greet = greet
        this.callback = callback
    }

    val client = OkHttpClient()


    /**
     * Sends the wish request. Note that this method does not yet handle all error cases:
     *  - Bitte Wunsch/Gruss und einen Nick angeben
     *  - Wunsch/Gruss wurde übermittelt
     *  TODO Fix this
     */
    fun send() {
        HyperLog.d(TAG, "send() - Starting to build request.")
        var requestBodyBuilder = MultipartBuilder().type(MultipartBuilder.FORM)

        if (!TextUtils.isEmpty(nick)) {
            requestBodyBuilder = requestBodyBuilder.addFormDataPart(KEY_NICK, nick)
        }
        if (!TextUtils.isEmpty(artist)) {
            requestBodyBuilder = requestBodyBuilder.addFormDataPart(KEY_ARTIST, artist)
        }
        if (!TextUtils.isEmpty(title)) {
            requestBodyBuilder = requestBodyBuilder.addFormDataPart(KEY_SONG, title)
        }
        if (!TextUtils.isEmpty(greet)) {
            requestBodyBuilder = requestBodyBuilder.addFormDataPart(KEY_GREET, greet)
        }

        val requestBody = requestBodyBuilder
                .build()

        val request = Request.Builder()
                .url(BuildConfig.METAL_ONLY_WUNSCHSCRIPT_POST_URL)
                .method("POST", RequestBody.create(null, ByteArray(0)))
                .post(requestBody)
                .build()


        HyperLog.d(TAG, "send() - Built request. Sending...")
        try {
            val response = client.newCall(request).execute()

            HyperLog.d(TAG, "send() - Received response.")
            val isSuccessResponseBody = response.body()?.string()?.contains(BuildConfig.WISH_SUCCESS) ?: false
            if (response.code() == 200 && isSuccessResponseBody) {
                HyperLog.d(TAG, "send() - Response was 'success'. Doing onSuccess-callback.")
                callback.onSuccess()
            } else {
                HyperLog.d(TAG, "send() - Response was 'failure'. Doing onFail-callback.")
                callback.onFail()
            }
        } catch (e: IOException) {
            HyperLog.d(TAG, "send() - Got an IOException. Doing onException-callback.")
            callback.onException(e)
        }
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
