package de.ironjan.metalonly.api

import android.content.Context
import arrow.core.Either
import com.koushikdutta.ion.Ion
import com.koushikdutta.ion.builder.Builders
import de.ironjan.metalonly.api.model.Stats
import de.ironjan.metalonly.api.model.TrackInfo
import de.ironjan.metalonly.api.model.ShowInfo
import de.ironjan.metalonly.log.LW
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class Client(private val context: Context) {

    fun getStats(): Either<String, Stats> = safeRequest(statsUrl, Stats::class.java)

    fun getTrack(): Either<String, TrackInfo> = safeRequest(trackUrl, TrackInfo::class.java)

    fun getShowInfo(): Either<String, ShowInfo> = safeRequest(showInfoUrl, ShowInfo::class.java)

//    fun getMods(noCache: Boolean) = safeRequest(modsUrl, noCache)
//    fun getPlan(noCache: Boolean) = safeRequest(planUrl, noCache)

    private fun <T> safeRequest(url: String, clazz: Class<T>): Either<String, T> {
        return try {
            val right = prepareRequest(url, true)
                    .`as`(clazz)
                    .get(REQUEST_TIMEOUT_30_SECONDS, TimeUnit.SECONDS)

            Either.right(right)
        } catch (e: Exception) {
            wrapException(e)
        }
    }

    private fun wrapException(e: Exception): Either<String, Nothing> {
        val sw: Writer = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        LW.e("Client", "Request failed: ", e)
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
        private const val baseUrl = "https://mensaupb.herokuapp.com/metalonly"

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


        fun initIon(context: Context) {
            val default = Ion.getDefault(context)

            default.conscryptMiddleware.enable(true)

            val sslSocketMiddleware = default.httpClient.sslSocketMiddleware

            val tms = Array<TrustManager>(1) {
                object : X509TrustManager {
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    }

                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                }
            }
            sslSocketMiddleware.setTrustManagers(tms)


        }
    }
}