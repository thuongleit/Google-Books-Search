package me.thuongle.googlebookssearch.repository

import me.thuongle.googlebookssearch.api.BookService
import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.model.LiveResult
import me.thuongle.googlebookssearch.testing.OpenForTesting
import me.thuongle.googlebookssearch.util.AppExecutors
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class BookRepository @Inject constructor(
    private var service: BookService,
    val appExecutors: AppExecutors
) {

    fun search(query: String): LiveResult<List<GoogleBook>> {
        Timber.d("Search books [type=${service.getType()}, query=$query]")
        return object : NetworkHandler<List<GoogleBook>>(appExecutors) {
            override fun execute(): List<GoogleBook>? {
                return service.searchBooks(query).items
                    .also {
                        Timber.d("Search books: [Results=$it]")
                    }
            }
        }.result
    }

    fun searchWithUrl(url: String): LiveResult<List<GoogleBook>> {
        Timber.d("Search books: [type=${service.getType()}, url=$url]")
        return object : NetworkHandler<List<GoogleBook>>(appExecutors) {
            override fun execute(): List<GoogleBook>? {
                return service.searchBooksWithUrl(url).items
                    .also {
                        Timber.d("Search books: [Results=$it]")
                    }
            }
        }.result
    }

    fun swapService(newType: BookService.NetworkExecutorType) {
        service.swapService(newType)
    }

    fun getService() = service
}