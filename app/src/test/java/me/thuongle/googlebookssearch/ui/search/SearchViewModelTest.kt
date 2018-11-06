package me.thuongle.googlebookssearch.ui.search

import android.arch.core.executor.testing.InstantTaskExecutorRule
import me.thuongle.googlebookssearch.repository.BookRepository
import me.thuongle.googlebookssearch.util.mock
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SearchViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock<BookRepository>()
    private var viewModel = SearchViewModel(repository)

    @Test
    fun `test default values in init`() {
        assertThat(viewModel.searchResult, notNullValue())
        verify(repository, never()).search(anyString())
        verify(repository, never()).searchWithUrl(anyString())
    }


    @Test
    fun `don't execute without observers`() {
        viewModel.searchBooks("foo")
        verify(repository, never()).search(anyString())
        verify(repository, never()).searchWithUrl(anyString())
    }

    @Test
    fun `execute querying when observed`() {
        viewModel.searchBooks("foo")
        viewModel.searchResult.observeForever(mock())
        verify(repository).search("foo")
        verify(repository, never()).searchWithUrl(anyString())
    }

    @Test
    fun `execute querying with url when observed`() {
        viewModel.searchBooks("https://www.google.com")
        viewModel.searchResult.observeForever(mock())
        verify(repository).searchWithUrl("https://www.google.com")
        verify(repository, never()).search(anyString())
    }

    @Test
    fun `re-execute when changing query while observed`() {
        viewModel.searchResult.observeForever(mock())
        viewModel.searchBooks("foo")
        viewModel.searchBooks("bar")

        verify(repository).search("foo")
        verify(repository).search("bar")
    }

    @Test
    fun `not execute when query the same text while observed`() {
        viewModel.searchResult.observeForever(mock())
        viewModel.searchBooks("foo")
        viewModel.searchBooks("foo")

        verify(repository, only()).search("foo")
    }

    @Test
    fun retry() {
        viewModel.retryQuery()
        verifyNoMoreInteractions(repository)
        viewModel.searchBooks("foo")
        verifyNoMoreInteractions(repository)
        viewModel.searchResult.observeForever(mock())
        verify(repository).search("foo")
        reset(repository)
        viewModel.retryQuery()
        verify(repository).search("foo")
    }

}