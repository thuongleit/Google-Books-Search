package me.thuongle.googlebookssearch.api

import me.thuongle.googlebookssearch.util.mock
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import retrofit2.Call
import retrofit2.Response

@RunWith(JUnit4::class)
class BookServiceTest {

    private lateinit var service: BookService
    private var mockRetrofitService = mock<GoogleBooksRetrofitService>()
    private var mockLegacyService = mock<GoogleBooksLegacyService>()

    @Before
    fun setUp(){
        service = BookServiceImpl(mockLegacyService, mockRetrofitService)
    }

    @Test
    fun `search books by query with legacy service`() {
        service.swapService(BookService.NetworkExecutorType.LEGACY)
        service.searchBooks("hello", 20, 30)
        verify(mockLegacyService).searchBooks("hello", 20, 30)
    }

    @Test
    fun `search books by query with retrofit service`() {
        service.swapService(BookService.NetworkExecutorType.RETROFIT)
        val mockCall = mock<Call<GoogleVolumeResponse>>()
        val response = Response.success(GoogleVolumeResponse.createEmpty())
        `when`(
            mockRetrofitService.searchBooks(
                "hello",
                20,
                30
            )
        ).thenReturn(mockCall)
        `when`(mockCall.execute()).thenReturn(response)
        service.searchBooks("hello", 20, 30)
        verify(mockRetrofitService).searchBooks("hello", 20, 30)
    }

    @Test
    fun `search books by url with legacy service`() {
        service.swapService(BookService.NetworkExecutorType.LEGACY)
        service.searchBooksWithUrl("/")
        verify(mockLegacyService).searchBooksWithUrl("/")
    }

    @Test
    fun `search books by url with retrofit service`() {
        service.swapService(BookService.NetworkExecutorType.RETROFIT)
        val mockCall = mock<Call<GoogleVolumeResponse>>()
        val response = Response.success(GoogleVolumeResponse.createEmpty())
        `when`(mockRetrofitService.searchBooksWithUrl("/")).thenReturn(mockCall)
        `when`(mockCall.execute()).thenReturn(response)
        service.searchBooksWithUrl("/")
        verify(mockRetrofitService).searchBooksWithUrl("/")
    }

    @Test
    fun `get service type`() {
        service.swapService(BookService.NetworkExecutorType.RETROFIT)
        assertThat(service.getType(), `is`(BookService.NetworkExecutorType.RETROFIT))
    }

    @Test
    fun `get service instance, return legacy`() {
        service.swapService(BookService.NetworkExecutorType.LEGACY)
        assertTrue((service as BookServiceImpl).getService() is GoogleBooksLegacyService)
    }

    @Test
    fun `get service instance, return retrofit`() {
        service.swapService(BookService.NetworkExecutorType.RETROFIT)
        assertTrue((service as BookServiceImpl).getService() is GoogleBooksRetrofitService)
    }
}