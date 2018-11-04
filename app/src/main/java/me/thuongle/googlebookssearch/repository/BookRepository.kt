package me.thuongle.googlebookssearch.repository

import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.api.GoogleBooksService
import me.thuongle.googlebookssearch.model.LiveResult
import me.thuongle.googlebookssearch.util.AppExecutors
import timber.log.Timber

class BookRepository private constructor(
    val service: GoogleBooksService,
    val appExecutors: AppExecutors
) {

    fun search(query: String): LiveResult<List<GoogleBook>> {
        Timber.d("Search books with q=$query")
        return object : NetworkHandler<List<GoogleBook>>(appExecutors) {
            override fun requestService(): List<GoogleBook>? {
                val response = service.searchBooks(query).execute()
                return response.body()?.items
                    .also {
                        Timber.d("Search books result: $it")
                    }
            }
        }.result
    }

    companion object {
        @Volatile
        private var instance: BookRepository? = null

        fun getInstance(): BookRepository {
            return instance ?: synchronized(this) {
                instance ?: BookRepository(GoogleBooksService.create()!!, AppExecutors()).also { instance = it }
            }
        }
    }
}