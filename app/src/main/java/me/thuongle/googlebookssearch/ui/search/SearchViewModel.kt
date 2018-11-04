package me.thuongle.googlebookssearch.ui.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.model.AbsentLiveData
import me.thuongle.googlebookssearch.model.LiveResult
import me.thuongle.googlebookssearch.model.NetworkStatus
import me.thuongle.googlebookssearch.repository.BookRepository
import me.thuongle.googlebookssearch.util.map
import me.thuongle.googlebookssearch.util.switchMap
import java.util.*

class SearchViewModel(private val bookRepository: BookRepository) : ViewModel() {
    private val query = MutableLiveData<String>()

    val searchResult: LiveResult<List<GoogleBook>> = query.switchMap { queryText ->
        if (queryText.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            bookRepository.search(queryText)
        }
    }

    val booksResult: LiveData<List<GoogleBook>?> = searchResult.map { it.data }
    val networkStatus: LiveData<NetworkStatus?> = searchResult.map { it.status }

    fun searchBooks(originalQuery: String) {
        val input = originalQuery.toLowerCase(Locale.getDefault()).trim()
        if (input == query.value) {
            return
        }
        query.value = input
    }
}