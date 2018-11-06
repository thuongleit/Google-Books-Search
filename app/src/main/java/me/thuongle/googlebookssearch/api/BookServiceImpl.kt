package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread
import retrofit2.Response
import java.io.IOException
import java.net.UnknownServiceException

class BookServiceImpl private constructor(val networkExecutorType: BookService.NetworkExecutorType) : BookService {

    @WorkerThread
    @Throws(Exception::class)
    override fun searchBooks(query: String, startIndex: Int, maxResults: Int): GoogleVolumeResponse {
        return getService().let { service ->
            when (service) {
                is GoogleBooksRetrofitService -> {
                    service
                        .searchBooks(query, startIndex, maxResults)
                        .execute()
                        .handleResponse() ?: GoogleVolumeResponse.createEmpty()
                }
                is GoogleBooksLegacyService -> {
                    service.searchBooks(query, startIndex, maxResults)
                }
                else -> throw UnknownServiceException("Unknown given BookService")
            }
        }
    }

    override fun searchBooksWithUrl(url: String): GoogleVolumeResponse {
        return getService().let { service ->
            when (service) {
                is GoogleBooksRetrofitService -> {
                    service
                        .searchBooks(url)
                        .execute()
                        .handleResponse() ?: GoogleVolumeResponse.createEmpty()
                }
                is GoogleBooksLegacyService -> {
                    service.searchBooksWithUrl(url)
                }
                else -> throw UnknownServiceException("Unknown given BookService")
            }
        }
    }

    override fun getType(): BookService.NetworkExecutorType = networkExecutorType

    fun getService(): Any {
        return when (networkExecutorType) {
            BookService.NetworkExecutorType.RETROFIT -> GoogleBooksRetrofitService.create()
            BookService.NetworkExecutorType.LEGACY -> GoogleBooksLegacyService.create(RestClient())
        }
    }

    companion object {
        fun create(type: BookService.NetworkExecutorType): BookServiceImpl {
            return BookServiceImpl(type)
        }
    }
}

fun <T> Response<T>.handleResponse(): T? {
    return if (this.isSuccessful) {
        this.body()
    } else {
        throw IOException("${this.code()}:${this.message()}")
    }
}