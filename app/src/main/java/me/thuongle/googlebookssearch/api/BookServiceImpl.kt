package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread
import java.io.IOException
import java.net.UnknownServiceException

class BookServiceImpl private constructor(val networkExecutorType: BookService.NetworkExecutorType) : BookService {


    @WorkerThread
    @Throws(Exception::class)
    override fun searchBooks(query: String, startIndex: Int, maxResults: Int): GoogleVolumeResponse {
        return getService().let {
            when (it) {
                is GoogleBooksRetrofitService -> {
                    val response = it
                        .searchBooks(query, startIndex, maxResults)
                        .execute()
                    if (response.isSuccessful) {
                        response.body() ?: GoogleVolumeResponse.createEmpty()
                    } else {
                        throw IOException("${response.code()}:${response.message()}")
                    }
                }
                is GoogleBooksLegacyService -> {
                    it.searchBooks(query, startIndex, maxResults)
                }
                else -> {
                    throw UnknownServiceException("Unknown given BookService")
                }
            }
        }
    }

    override fun getType(): BookService.NetworkExecutorType = networkExecutorType

    fun getService(): Any {
        return when (networkExecutorType) {
            BookService.NetworkExecutorType.RETROFIT -> GoogleBooksRetrofitService.create()
            BookService.NetworkExecutorType.LEGACY -> GoogleBooksLegacyService.create()
        }
    }

    companion object {
        fun create(type: BookService.NetworkExecutorType): BookServiceImpl {
            return BookServiceImpl(type)
        }
    }
}