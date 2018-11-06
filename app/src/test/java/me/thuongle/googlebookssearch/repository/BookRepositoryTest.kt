package me.thuongle.googlebookssearch.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.google.gson.Gson
import me.thuongle.googlebookssearch.api.BookService
import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.api.GoogleVolumeResponse
import me.thuongle.googlebookssearch.model.Result
import me.thuongle.googlebookssearch.util.InstantAppExecutors
import me.thuongle.googlebookssearch.util.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.io.IOException

@RunWith(JUnit4::class)
class BookRepositoryTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: BookRepository
    private val service = mock<BookService>()

    @Before
    fun setUp() {
        repository = BookRepository(service, InstantAppExecutors())
    }

    @Test
    fun `search books by query, return success`() {
        val query = "book"
        val response = getResponse()
        `when`(service.searchBooks(query)).thenReturn(response)

        val observer = mock<Observer<Result<List<GoogleBook>>>>()
        val result = repository.search(query)
        result.observeForever(observer)
        verify(observer).onChanged(Result.success(response.items))
    }

    @Test
    fun `search books by query, return error`() {
        val query = "book"
        val error = IOException("Error")
        `when`(service.searchBooks(query)).thenThrow(error)

        val observer = mock<Observer<Result<List<GoogleBook>>>>()
        val result = repository.search(query)
        result.observeForever(observer)
        verify(observer).onChanged(Result.error(error.message!!, error))
    }

    @Test
    fun `search books by url, return success`() {
        val response = getResponse()
        `when`(service.searchBooksWithUrl("/")).thenReturn(response)

        val observer = mock<Observer<Result<List<GoogleBook>>>>()
        val result = repository.searchWithUrl("/")
        result.observeForever(observer)
        verify(observer).onChanged(Result.success(response.items))
    }

    @Test
    fun `search books by url, return error`() {
        val error = IOException("Error")
        `when`(service.searchBooksWithUrl("/")).thenThrow(error)

        val observer = mock<Observer<Result<List<GoogleBook>>>>()
        val result = repository.searchWithUrl("/")
        result.observeForever(observer)
        verify(observer).onChanged(Result.error(error.message!!, error))
    }

    private fun getResponse(): GoogleVolumeResponse {
        return javaClass.classLoader?.getResourceAsStream("api-response/books_search.json")?.let {
            Gson().fromJson(it.reader(Charsets.UTF_8), GoogleVolumeResponse::class.java)
        }!!
    }
}