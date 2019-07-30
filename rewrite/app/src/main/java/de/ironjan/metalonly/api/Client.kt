package de.ironjan.metalonly.api

import android.content.Context
import arrow.core.Either
import com.google.gson.reflect.TypeToken
import com.koushikdutta.ion.Ion
import com.koushikdutta.ion.builder.Builders
import de.ironjan.metalonly.api.model.Stats
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.concurrent.TimeUnit

class Client(private val context: Context) {

    fun getStats() = safeRequest(statsUrl, true)

    fun getShowInfo() = safeRequest(showInfoUrl, true)

    fun getMods(noCache: Boolean) = safeRequest(modsUrl, noCache)

    fun getPlan(noCache: Boolean) = safeRequest(planUrl, noCache)

    fun getTrack() = safeRequest(trackUrl, true)

    private fun safeRequest(url: String, noCache: Boolean): Either<String, Stats> =
        try {
            Either.right(tryExecute(prepareRequest(url, noCache)))
        } catch (e: Exception) {
            wrapException(e)
        }

    private fun <T> tryExecute(preparedRequest: Builders.Any.B): T =
        preparedRequest
            .`as`(object : TypeToken<T>() {})
            .get(REQUEST_TIMEOUT_30_SECONDS, TimeUnit.SECONDS)

    private fun wrapException(e: Exception): Either<String, Nothing> {
        val sw: Writer = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return Either.left(sw.toString())
    }


    private fun prepareRequest(url: String, forceReload: Boolean): Builders.Any.B {
        val requestBuilder = Ion.with(context).load(url)
//                .setLogging("ClientV3", Log.DEBUG)
        return if (forceReload) {
            requestBuilder.noCache()
        } else {
            requestBuilder
        }
    }


    companion object {
        const val REQUEST_TIMEOUT_30_SECONDS = 30000L
        private const val baseUrl = "https://mensaupb.herokuapp.com/api"

        private const val statsPath = "/stats"

        private const val trackPath = "/track"
        private const val showInformationPath = "/showinformation"
        private const val planPath = "/plan"
        private const val modsPath = "/mods"

        const val statsUrl = "$baseUrl$statsPath"
        const val showInfoUrl = "$baseUrl$showInformationPath"
        const val trackUrl = "$baseUrl$trackPath"
        const val planUrl = "$baseUrl$planPath"
        const val modsUrl = "$baseUrl$modsPath"
    }
}