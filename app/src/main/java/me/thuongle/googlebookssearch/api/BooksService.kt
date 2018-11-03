package me.thuongle.googlebookssearch.api

/**
 * Interface for a books API
 */
interface BooksService {

    fun searchBooks(query: String, startIndex: Int): Any
}