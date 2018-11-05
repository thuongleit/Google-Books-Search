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

    enum class ServiceType {
        RETROFIT,
        LEGACY
    }
}