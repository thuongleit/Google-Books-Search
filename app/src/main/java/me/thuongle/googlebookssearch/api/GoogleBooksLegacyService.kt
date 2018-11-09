package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread
import com.google.gson.Gson
import me.thuongle.googlebookssearch.testing.OpenForTesting
import me.thuongle.googlebookssearch.util.toUrlEncodeUTF8

/**
 * REST API access points for Google Books API using HttpURLConnection
 */
@OpenForTesting
class GoogleBooksLegacyService(val client: RestClient) {

    @WorkerThread
    @Throws(Exception::class)
    fun searchBooks(query: String, startIndex: Int = 0, maxResults: Int = 40): GoogleVolumeResponse {
        val parameters = mapOf(
            "q" to query,
            "startIndex" to startIndex,
            "maxResults" to maxResults
        )

        return client.execute(
            requestUrl = "${GOOGLE_BOOK_API_ENDPOINT}volumes?${parameters.toUrlEncodeUTF8()}",
            processResponse = {
                Gson().fromJson(it, GoogleVolumeResponse::class.java)
            })
    }

    fun searchBooksWithUrl(url: String): GoogleVolumeResponse {
        return client.execute(
            requestUrl = url,
            processResponse = {
                Gson().fromJson(it, GoogleVolumeResponse::class.java)
            })
    }
}