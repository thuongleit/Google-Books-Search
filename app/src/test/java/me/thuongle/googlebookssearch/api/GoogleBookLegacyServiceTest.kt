package me.thuongle.googlebookssearch.api

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@RunWith(JUnit4::class)
class GoogleBookLegacyServiceTest {
    private lateinit var service: GoogleBooksLegacyService
    private lateinit var server: MockWebServer

    @Before
    fun createService() {
        server = MockWebServer()
        val client = object : RestClient() {
            override fun buildUrl(url: String): URL {
                return server.url("/v1/book/").url()
            }
        }
        service = GoogleBooksLegacyService(client)
    }

    @After
    fun stopService() {
        server.shutdown()
    }

    @Test
    fun `search books by query, return 200 OK`() {
        enqueueResponse("books_search.json")
        val client = object : RestClient() {
            override fun buildUrl(url: String): URL {
                //remove GOOGLE_BOOK_API_ENDPOINT to not request to real url
                return server.url(url.replace(GOOGLE_BOOK_API_ENDPOINT, "")).url()
            }
        }
        service = GoogleBooksLegacyService(client)
        val response = service.searchBooks("books", 10)

        val request = server.takeRequest()
        assertThat(request.requestLine, `is`("GET /volumes?q=books&startIndex=10&maxResults=40 HTTP/1.1"))

        assertResponse(response)
    }

    @Test
    fun `search books by url, return 200 OK`() {
        enqueueResponse("books_search.json")
        val response = service.searchBooksWithUrl("/v1/book")

        val request = server.takeRequest()
        assertThat(request.requestLine, `is`("GET /v1/book/ HTTP/1.1"))

        assertResponse(response)
    }

    @Test
    fun `search books by query, return error`() {
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .let {
                server.enqueue(it)
            }
        try {
            service.searchBooks("books", 10)
            fail("Action should fail")
        } catch (e: Exception) {
            assertTrue(e is IOException)
            assertThat(e.message, `is`("404:Client Error"))
        }
    }

    @Test
    fun `search books by url, return error`() {
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .let {
                server.enqueue(it)
            }
        try {
            service.searchBooksWithUrl("/")
            fail("Action should fail")
        } catch (e: Exception) {
            assertTrue(e is IOException)
            assertThat(e.message, `is`("404:Client Error"))
        }
    }

    private fun assertResponse(body: GoogleVolumeResponse?) {
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
            server.enqueue(
                mockResponse
                    .setBody(source.readString(Charsets.UTF_8))
            )
        }
    }
}