package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread
import com.google.gson.Gson
import me.thuongle.googlebookssearch.util.toUrlEncodeUTF8

/**
 * REST API access points for Google Books API using HttpURLConnection
 */
class GoogleBooksLegacyService private constructor() {

    @WorkerThread
    @Throws(Exception::class)
    fun searchBooks(query: String, startIndex: Int, maxResults: Int): GoogleVolumeResponse {
        return RestClient.create().let { client ->
            val parameters = mapOf(
                "q" to query,
                "startIndex" to startIndex,
                "maxResults" to maxResults
            )

            client.execute(
                requestUrl = "${GOOGLE_BOOK_API_ENDPOINT}volumes?${parameters.toUrlEncodeUTF8()}",
                processResponse = {
                    Gson().fromJson(it, GoogleVolumeResponse::class.java)
                })
        }
    }

    companion object {
        fun create(): GoogleBooksLegacyService {
            return GoogleBooksLegacyService()
        }
    }
}