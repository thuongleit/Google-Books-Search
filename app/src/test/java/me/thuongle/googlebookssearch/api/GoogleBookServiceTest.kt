package me.thuongle.googlebookssearch.api

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class GoogleBookServiceTest {
    private lateinit var service: GoogleBooksService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun searchBooks() {
        enqueueResponse("books_search.json")
        val response = service.searchBooks("books", 10).execute()

        //test request
        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/volumes?q=books&startIndex=10&maxResults=40"))

        //test response
        val body = response.body()

        assertThat(body, `is`(notNullValue()))
        assertThat(body!!.kind, `is`("books#volumes"))
        assertThat(body.items.size, `is`(2))
        assertThat(body.totalItems, `is`(512))

        val firstBook = body.items[0]
        assertThat(firstBook.kind, `is`("books#volume"))
        assertThat(firstBook.id, `is`("id_1"))
        assertThat(firstBook.etag, `is`("etag_1"))
        assertThat(firstBook.selfLink, `is`("selfLink_1"))
        firstBook.bookInfo.let { bookInfo ->
            assertThat(bookInfo.title, `is`("title_1"))
            assertThat(bookInfo.subtitle, `is`("subtitle_1"))
            assertThat(bookInfo.authorsList.size, `is`(2))
            assertThat(bookInfo.authorsList[0], `is`("author_1"))
            assertThat(bookInfo.authorsList[1], `is`("author_2"))
            assertThat(bookInfo.authors, `is`("author_1, author_2"))
            assertThat(bookInfo.publisher, `is`("publisher_1"))
            assertThat(bookInfo.publishedDate, `is`("2000"))
            assertThat(bookInfo.description, `is`("description_1"))
            assertThat(bookInfo.industryIdentifiers.size, `is`(2))
            assertThat(bookInfo.industryIdentifiers[0].type, `is`("ISBN_10"))
            assertThat(bookInfo.industryIdentifiers[0].identifier, `is`("157356107X"))
            assertThat(bookInfo.pageCount, `is`(682))
            assertThat(bookInfo.printType, `is`("BOOK"))
            assertThat(bookInfo.categories.size, `is`(2))
            assertThat(bookInfo.categories[0], `is`("Computers"))
            assertThat(bookInfo.categories[1], `is`("Books"))
            assertThat(bookInfo.imageLinks, `is`(notNullValue()))
            assertThat(bookInfo.imageLinks.smallThumbnail, `is`("smallThumbnail_1"))
            assertThat(bookInfo.imageLinks.thumbnail, `is`("thumbnail_1"))
            assertThat(bookInfo.imageLinks.small, `is`("small_1"))
            assertThat(bookInfo.imageLinks.medium, `is`("medium_1"))
            assertThat(bookInfo.imageLinks.large, `is`("large_1"))
            assertThat(bookInfo.imageLinks.extraLarge, `is`("extraLarge_1"))
            assertThat(bookInfo.language, `is`("en"))
            assertThat(bookInfo.previewLink, `is`("previewLink_1"))
            assertThat(bookInfo.infoLink, `is`("infoLink_1"))
            assertThat(bookInfo.canonicalVolumeLink, `is`("canonicalVolumeLink_1"))
        }
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        javaClass.classLoader?.getResourceAsStream("api-response/$fileName")?.let {
            val source = Okio.buffer(Okio.source(it))
            val mockResponse = MockResponse()
            for ((key, value) in headers) {
                mockResponse.addHeader(key, value)
            }
            mockWebServer.enqueue(
                mockResponse
                    .setBody(source.readString(Charsets.UTF_8))
            )
        }
    }
}