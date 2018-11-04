package me.thuongle.googlebookssearch.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points for Google Books API
 */
interface GoogleBooksService {

    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 40
    ): Call<GoogleVolumeResponse>

    companion object {
        fun create(): GoogleBooksService? {
            return Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleBooksService::class.java)
        }
    }
}