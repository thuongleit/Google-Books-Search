package me.thuongle.googlebookssearch.api

import android.support.annotation.WorkerThread

const val GOOGLE_BOOK_API_ENDPOINT = "https://www.googleapis.com/books/v1/"

interface BookService {

    @WorkerThread
    @Throws(Exception::class)
    fun searchBooks(
        query: String,
        startIndex: Int = 0,
        maxResults: Int = 40
    ): GoogleVolumeResponse

    @WorkerThread
    @Throws(Exception::class)
    fun searchBooksWithUrl(url: String): GoogleVolumeResponse

    fun getType(): NetworkExecutorType

    fun swapService(newType: BookService.NetworkExecutorType)

    enum class NetworkExecutorType {
        RETROFIT,
        LEGACY;

        override fun toString(): String {
            return name.toLowerCase()
        }

        companion object {
            fun swap(input: NetworkExecutorType): NetworkExecutorType {
                return when (input) {
                    LEGACY -> RETROFIT
                    RETROFIT -> LEGACY
                }
            }
        }
    }
}