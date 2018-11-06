package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread
import com.google.gson.Gson
import me.thuongle.googlebookssearch.util.toUrlEncodeUTF8

/**
 * REST API access points for Google Books API using HttpURLConnection
 */
class GoogleBooksLegacyService(val client: RestClient) : BookService {

    @WorkerThread
    @Throws(Exception::class)
    override fun searchBooks(query: String, startIndex: Int, maxResults: Int): GoogleVolumeResponse {
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

    override fun searchBooksWithUrl(url: String): GoogleVolumeResponse {
        return client.execute(
            requestUrl = url,
            processResponse = {
                Gson().fromJson(it, GoogleVolumeResponse::class.java)
            })
    }

    override fun getType(): BookService.NetworkExecutorType = BookService.NetworkExecutorType.LEGACY

    companion object {
        fun create(client: RestClient): GoogleBooksLegacyService {
            return GoogleBooksLegacyService(client)
        }
    }
}