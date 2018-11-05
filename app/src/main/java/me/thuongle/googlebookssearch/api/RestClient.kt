package me.thuongle.googlebookssearch.api

import android.support.annotation.StringDef
import android.support.annotation.VisibleForTesting
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class RestClient {

    @StringDef(GET, PUT, POST, DELETE, PATCH, OPTIONS)
    annotation class RequestMethod

    @Throws(IOException::class)
    fun <T> execute(
        requestUrl: String,
        @RequestMethod requestMethod: String = GET,
        requestParameters: JSONObject? = null,
        processResponse: (String) -> T
    ): T {
        val url = buildUrl(requestUrl)
        val response = execute(url, requestMethod, requestParameters)

        return when (response.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val body = response.inputStream.reader().readText()
                Timber.d("Request succeed: $body")
                processResponse(body)
            }
            else -> {
                Timber.e("Request failed: ${response.responseCode}:${response.responseMessage}")
                throw IOException("${response.responseCode}:${response.responseMessage}")
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    open fun buildUrl(url: String): URL = URL(url)

    @Throws(IOException::class)
    private fun execute(url: URL, @RequestMethod requestMethod: String, parameters: JSONObject?): HttpURLConnection {
        val startTime = System.currentTimeMillis()
        val connection = openConnection(url, requestMethod, parameters)
        Timber.d("Request: ${connection.url} ${connection.requestMethod} took ${System.currentTimeMillis() - startTime}ms")
        return connection
    }

    private fun openConnection(
        url: URL,
        @RequestMethod requestMethod: String,
        parameters: JSONObject?
    ): HttpURLConnection {
        Timber.d("Opening HTTP connection...")
        val connection = url.openConnection() as HttpURLConnection
        Timber.d("Connection is open.")

        connection.requestMethod = requestMethod
        if (requestMethod == POST) {
            connection.doOutput = true
        }
        Timber.d("Writing execute parameters: ${connection.requestProperties}")
        parameters?.let {
            val out = BufferedOutputStream(connection.outputStream)
            out.write(it.toString().toByteArray())
            out.flush()
            Timber.d("${parameters.toString().length} bytes written, content: $parameters")
        }

        return connection
    }

    companion object {
        internal const val GET = "GET"
        internal const val PUT = "PUT"
        internal const val POST = "POST"
        internal const val DELETE = "DELETE"
        internal const val PATCH = "PATCH"
        internal const val OPTIONS = "OPTIONS"

        fun create(): RestClient {
            return RestClient()
        }
    }
}
