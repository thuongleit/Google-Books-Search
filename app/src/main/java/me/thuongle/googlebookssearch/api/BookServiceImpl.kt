package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread
import java.io.IOException
import java.net.UnknownServiceException

class BookServiceImpl private constructor(val type: BookService.ServiceType) : BookService {

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

    fun getService(): Any {
        return when (type) {
            BookService.ServiceType.RETROFIT -> GoogleBooksRetrofitService.create()
            BookService.ServiceType.LEGACY -> GoogleBooksLegacyService.create()
        }
    }

    companion object {
        fun create(type: BookService.ServiceType): BookServiceImpl {
            return BookServiceImpl(type)
        }
    }
}