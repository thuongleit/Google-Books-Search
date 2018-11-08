package me.thuongle.googlebookssearch.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * REST API access points for Google Books API using Retrofit
 */
interface GoogleBooksRetrofitService {

    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 40
    ): Call<GoogleVolumeResponse>

    @GET
    fun searchBooksWithUrl(@Url url: String): Call<GoogleVolumeResponse>
}