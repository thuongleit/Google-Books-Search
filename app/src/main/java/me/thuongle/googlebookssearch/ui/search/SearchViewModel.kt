package me.thuongle.googlebookssearch.ui.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Patterns
import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.model.AbsentLiveData
import me.thuongle.googlebookssearch.model.LiveResult
import me.thuongle.googlebookssearch.repository.BookRepository
import me.thuongle.googlebookssearch.util.switchMap
import java.util.*

class SearchViewModel(val repository: BookRepository) : ViewModel() {
    private val query = MutableLiveData<String>()

    val searchResult: LiveResult<List<GoogleBook>> = query.switchMap { queryText ->
        if (queryText.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            if (Patterns.WEB_URL.matcher(queryText).matches()) {
                repository.searchWithUrl(queryText)
            } else {
                repository.search(queryText)
            }
        }
    }

    fun searchBooks(originalQuery: String) {
        val input = originalQuery.toLowerCase(Locale.getDefault()).trim()
        if (input == query.value) {
            return
        }
        query.value = input
    }

    fun retryQuery() {
        query.value?.let {
            query.value = it
        }
    }
}