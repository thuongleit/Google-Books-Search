package me.thuongle.googlebookssearch.api

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONObject
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
class RestClientTest {

    private lateinit var client: RestClient
    private lateinit var server: MockWebServer

    @Before
    fun createService() {
        server = MockWebServer()
        client = object : RestClient() {
            override fun buildUrl(url: String): URL {
                return server.url("/").url()
            }
        }
    }

    @After
    fun stopService() {
        server.shutdown()
    }

    @Test
    fun `execute with default parameters, return 200 OK`() {
        val mockResponse = MockResponse()
        server.enqueue(
            mockResponse.setBody(
                "mockResponse"
            )
        )
        val response = client.execute(
            requestUrl = "/",
            processResponse = { response -> response }
        )
        assertThat(response, `is`("mockResponse"))

        val request = server.takeRequest()
        assertThat(request.requestLine, `is`("GET / HTTP/1.1"))
        assertThat(request.body.size(), `is`(0L))
    }

    @Test
    fun `execute with input parameters, return 200 OK`() {
        val mockResponse = MockResponse()
        server.enqueue(
            mockResponse.setBody(
                "mockResponse"
            )
        )
        val response = client.execute(
            requestUrl = "/",
            requestMethod = RestClient.POST,
            requestParameters = object : JSONObject() {
                override fun toString(): String {
                    return """{"user":"A"}"""
                }
            },
            processResponse = { response -> response }
        )
        assertThat(response, `is`("mockResponse"))

        val request = server.takeRequest()
        assertThat(request.requestLine, `is`("POST / HTTP/1.1"))
        assertThat(request.body.readString(Charsets.UTF_8), `is`("""{"user":"A"}"""))
    }

    @Test
    fun `execute, return errors`() {
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
            .let {
                server.enqueue(it)
            }
        try {

            client.execute(
                requestUrl = "/",
                processResponse = { response -> response }
            )
            fail("Action should fail")
        } catch (e: Exception) {
            assertTrue(e is IOException)
            assertThat(e.message, `is`("500:Server Error"))
        }
    }
}