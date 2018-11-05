package me.thuongle.googlebookssearch.repository

import me.thuongle.googlebookssearch.api.BookService
import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.model.LiveResult
import me.thuongle.googlebookssearch.util.AppExecutors
import timber.log.Timber

class BookRepository private constructor(
    val service: BookService,
    val appExecutors: AppExecutors
) {

    fun search(query: String): LiveResult<List<GoogleBook>> {
        Timber.d("Search books with q=$query")
        return object : NetworkHandler<List<GoogleBook>>(appExecutors) {
            override fun requestService(): List<GoogleBook>? {
                return service.searchBooks(query).items
                    .also {
                        Timber.d("Search books-result: $it")
                    }
            }
        }.result
    }

    companion object {
        @Volatile
        private var instance: BookRepository? = null

        fun getInstance(bookService: BookService, appExecutors: AppExecutors): BookRepository {
            return instance ?: synchronized(this) {
                instance ?: BookRepository(bookService, appExecutors).also { instance = it }
            }
        }
    }
}