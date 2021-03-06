package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread
import me.thuongle.googlebookssearch.BuildConfig
import me.thuongle.googlebookssearch.testing.OpenForTesting
import retrofit2.Response
import java.io.IOException
import java.net.UnknownServiceException

@OpenForTesting
class BookServiceImpl(val legacyService: GoogleBooksLegacyService,
                      val retrofitService: GoogleBooksRetrofitService) : BookService {

    var networkExecutorType: BookService.NetworkExecutorType = BuildConfig.NETWORK_EXECUTOR_TYPE

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
                        .searchBooksWithUrl(url)
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
            BookService.NetworkExecutorType.RETROFIT -> retrofitService
            BookService.NetworkExecutorType.LEGACY -> legacyService
        }
    }

    override fun swapService(newType: BookService.NetworkExecutorType) {
        networkExecutorType = newType
    }
}

fun <T> Response<T>.handleResponse(): T? {
    return if (this.isSuccessful) {
        this.body()
    } else {
        throw IOException("${this.code()}:${this.message()}")
    }
}