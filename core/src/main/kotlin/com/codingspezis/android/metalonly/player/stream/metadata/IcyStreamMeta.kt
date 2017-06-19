package com.codingspezis.android.metalonly.player.stream.metadata

import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.util.HashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by r on 17.09.14.
 *
 *
 * CREDIT TO:
 * http://stackoverflow.com/questions/8970548/how-to-get-metadata-of-a-streaming-online-radio
 */
class IcyStreamMeta(private val SDK_INT: Int, val streamUrl: URL) {

    private var metadata: Map<String, String>? = null
    var isError: Boolean = false
        private set
    private var data: Map<String, String>? = null

    /**
     * Get artist using stream's title

     * @return String
     * *
     * @throws IOException
     */
    val artist: String
        @Throws(IOException::class)
        get() {
            data = getMetadata()

            if (!data!!.containsKey("StreamTitle"))
                return ""

            val streamTitle = data!!["StreamTitle"]
            val title = streamTitle?.substring(0, streamTitle.indexOf("-"))
            return title?.trim { it <= ' ' } ?: "Unknown Artist"
        }

    /**
     * Get streamTitle

     * @return String
     * *
     * @throws IOException
     */
    val streamTitle: String
        @Throws(IOException::class)
        get() {
            data = getMetadata()

            val title = data!!["StreamTitle"]

            return title ?: ""
        }

    /**
     * Get title using stream's title

     * @return String
     * *
     * @throws IOException
     */
    val title: String
        @Throws(IOException::class)
        get() {
            data = getMetadata()

            if (!data!!.containsKey("StreamTitle")) {
                return ""
            }

            val streamTitle = data!!["StreamTitle"]
            val title = streamTitle?.substring(streamTitle.indexOf("-") + 1)
            return title?.trim { it <= ' ' } ?: "Unknown Title"
        }

    @Throws(IOException::class)
    fun getMetadata(): Map<String, String>? {
        if (metadata == null) {
            refreshMeta()
        }

        return metadata
    }

    @Synchronized @Throws(IOException::class)
    fun refreshMeta() {
        retreiveMetadata()
    }

    @Synchronized @Throws(IOException::class)
    private fun retreiveMetadata() {
        val con: URLConnection
        if (SDK_INT >= KITKAT_VERSION_CODE)
            con = IcyURLConnection(streamUrl)
        else
            con = streamUrl!!.openConnection()

        con.setRequestProperty("Icy-MetaData", "1")
        con.setRequestProperty("Connection", "close")

        con.connect()

        var metaDataOffset = 0
        val headers = con.headerFields
        val stream = con.getInputStream()

        if (headers.containsKey("icy-metaint")) {
            // Headers are sent via HTTP
            metaDataOffset = Integer.parseInt(headers["icy-metaint"]?.get(0))
        } else {
            // Headers are sent within a stream
            val strHeaders = StringBuilder(INITIAL_STRING_BUFFER_SIZE)
            do {
                if (strHeaders.length >= MAX_STRING_BUFFER_SIZE) {
                    strHeaders.delete(0, MAX_STRING_BUFFER_SIZE / 2)
                }

                val charInt: Int = stream.read()

                strHeaders.append(charInt.toChar())

                val hasMinLength = strHeaders.length > 5
                val lastFourChars = strHeaders.substring(strHeaders.length - 4, strHeaders.length)
                val haveNotFoundEndOfTitle = !(hasMinLength && lastFourChars == "\r\n\r\n")
            } while (charInt != -1 && haveNotFoundEndOfTitle)

            // Match headers to get metadata offset within a stream
            val p = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n")
            val m = p.matcher(strHeaders.toString())
            if (m.find()) {
                metaDataOffset = Integer.parseInt(m.group(2))
            }
        }

        // In case no data was sent
        if (metaDataOffset == 0) {
            isError = true
            return
        }

        // Read metadata
        var count = 0
        var metaDataLength = 4080 // 4080 is the max length
        val metaData = StringBuilder()
        // Stream position should be either at the beginning or right after headers
        do {
            val b: Int = stream.read()

            count++

            // Length of the metadata
            if (count == metaDataOffset + 1) {
                metaDataLength = b * 16
            }

           val inData = metaDataOffset < count + 1 && count < metaDataOffset + metaDataLength
            if (inData) {
                if (b != 0) {
                    metaData.append(b.toChar())
                }
            }
            if (count > metaDataOffset + metaDataLength) {
                break
            }

        } while (b != -1)

        metadata = parseMetadata(metaData.toString())

        stream.close()
    }

    companion object {

        /**
         * The value of Build.VERSION_CODES.KITKAT inlined to decouple this class from android.os.Build
         */
        private val KITKAT_VERSION_CODE = 19

        /**
         * Arbitrary, high number to limit the StringBuffer's size that is used to extract metadata.
         * Hopefully this value is also small enough to prevent the OOM errors from [](https://github.com/ironjan/metal-only/issues/68>#68</a>.
     ) */
        private val MAX_STRING_BUFFER_SIZE = 4096
        private val INITIAL_STRING_BUFFER_SIZE = 128

        private fun parseMetadata(metaString: String): Map<String, String> {
            val metadata = HashMap<String, String>()
            val metaParts = metaString.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val p = Pattern.compile("(\\w+)='(.*)'$")
            var m: Matcher
            for (metaPart in metaParts) {
                m = p.matcher(metaPart)
                if (m.find()) {
                    metadata.put(m.group(1) as String, m.group(2) as String)
                }
            }

            return metadata
        }
    }
}