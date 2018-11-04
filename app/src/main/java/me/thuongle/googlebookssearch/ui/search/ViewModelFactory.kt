package me.thuongle.googlebookssearch.ui.search

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import me.thuongle.googlebookssearch.repository.BookRepository

class ViewModelFactory(private val repository: BookRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        SearchViewModel(repository) as T
}